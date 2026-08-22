package com.huaji.galgamebyhuaji.vignaAiFrame.service.impl;//package com.huaji.galgamebyhuaji.service.ai.impl;

import com.huaji.galgamebyhuaji.constant.PrefixConstant;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfig;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.AESEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.IdUtil;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.AiBastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig.API_KEY_PLACEHOLDER;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class AiBastServiceImpl implements AiBastService {
	private final AiClientConfigMapper aiClientConfigMapper;
	@Value("${spring.cloud.vault.kv.ai-path}")
	private String aiTokenPath;
	@Value("${spring.cloud.vault.kv.backend}")
	private String bastPath;
	private final VaultTemplate vaultTemplate;
	private final AESEncryptionUtil aesEncryptionUtil;
	
	private String dataPath (String sub) {
		return bastPath + "/data/" + aiTokenPath + "/" + sub;
	}
	
	private String metadataPath (String sub) {
		return bastPath + "/metadata/" + aiTokenPath + "/" + sub;
	}
	
	private Map<String, Object> wrapKv2Body (Map<String, Object> kvData) {
		return Map.of("data", kvData);
	}
	
	@Override
	public ReturnResult<AiClientConfigWithBLOBs> getList () {
		List<AiClientConfigWithBLOBs> val = aiClientConfigMapper.selectByExampleWithBLOBs(null);
		if ( !ListUtil.isNull(val) )
			val = val.stream().peek(v -> v.setApiKey(null)).toList();
		return ReturnResult.isTrue("返回成功", val, null);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ReturnResult<AiClientConfigWithBLOBs> add (AiClientConfigWithBLOBs config, String apiKey) {
		if ( MyStringUtil.isNull(apiKey) )
			throw new OperationException("密钥不可为空");
		config.setId(null);
		config.setApiKey(API_KEY_PLACEHOLDER);
		config.setCreatedAt(new Date());
		config.setUpdatedAt(new Date());
		if ( MyStringUtil.isNull(config.getCode()) )
			config.setCode(IdUtil.getRandomId(PrefixConstant.AiClientConfigPrefix));
		else {
			AiClientConfigExample aiClientConfigExample = new AiClientConfigExample();
			aiClientConfigExample.createCriteria().andCodeEqualTo(config.getCode());
			List<AiClientConfig> aiClientConfigs = aiClientConfigMapper.selectByExample(aiClientConfigExample);
			if ( !ListUtil.isNull(aiClientConfigs) )
				throw new OperationException(
						"添加失败!因为使用的代码:%s,不唯一,冲突配置:%s".formatted(config.getCode(),
								aiClientConfigs.stream().map(AiClientConfig::getCode).toList()));
		}
		if ( Boolean.TRUE.equals(config.getKeyIsVault()) ) {
			WriteError.tryWrite(aiClientConfigMapper.insert(config));
			if ( config.getId() == null )
				throw new OperationException("数据库读写失败");
			String jsonName = String.format("%s-%s-%s",
					AiMerchantType.getByTypeNum(config.getMerchant()).getProviderName(),
					config.getId(),
					new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
			String path = dataPath(jsonName);
			config.setKeyIsVault(true);
			config.setApiKey(jsonName);
			try {
				WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeyWithBLOBs(config));
			} catch ( Exception e ) {
				vaultTemplate.delete(metadataPath(jsonName));
				throw e;
			}
			vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
		} else {
			config.setApiKey(aesEncryptionUtil.encryptValue(apiKey));
			config.setKeyIsVault(false);
			WriteError.tryWrite(aiClientConfigMapper.insert(config));
		}
		return ReturnResult.isTrue("新建成功", config);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ReturnResult<AiClientConfigWithBLOBs> update (AiClientConfigWithBLOBs aiClientConfig, String apiKey) {
		Boolean keyIsVault = aiClientConfig.getKeyIsVault();
		AiClientConfigWithBLOBs old = aiClientConfigMapper.selectByPrimaryKey(aiClientConfig.getId());
		aiClientConfig.setCode(null);
		if ( old == null || old.getId() == null )
			throw new OperationException("错误不存在的数据");
		if ( Boolean.TRUE.equals(keyIsVault) ) {
			if ( MyStringUtil.isNull(apiKey) )
				throw new OperationException("修改密钥托管时必须重新修改密钥!");
			if ( old.getKeyIsVault() ) {
				String path = dataPath(old.getApiKey());
				WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
				vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
			} else {
				String jsonName = String.format("%s-%s-%s",
						AiMerchantType.getByTypeNum(aiClientConfig.getMerchant()).getProviderName(),
						aiClientConfig.getId(),
						new SimpleDateFormat("-yyyy-MM-dd-HH-mm").format(new Date()));
				String path = dataPath(jsonName);
				aiClientConfig.setApiKey(jsonName);
				WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
				vaultTemplate.write(path, wrapKv2Body(Map.of("api-Key", apiKey)));
			}
		} else {
			boolean hasKey = !MyStringUtil.isNull(apiKey);
			if ( hasKey ) {
				aiClientConfig.setApiKey(aesEncryptionUtil.encryptValue(apiKey));
			} else {
				aiClientConfig.setKeyIsVault(old.getKeyIsVault());
				aiClientConfig.setApiKey(old.getApiKey());
			}
			try {
				WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfig));
			} catch ( Exception e ) {
				log.error("更新数据库时出错", e);
				throw new OperationException("密钥更新成功,但是其余部分更新失败");
			}
			if ( old.getKeyIsVault() && hasKey ) {
				vaultTemplate.delete(metadataPath(old.getApiKey()));
			}
		}
		return ReturnResult.isTrue("修改完成", aiClientConfig);
	}
	
	@Override
	@Transactional
	public ReturnResult<Void> updateState (Long id, boolean newState) {
		AiClientConfigWithBLOBs aiClientConfigWithBLOBs = new AiClientConfigWithBLOBs();
		aiClientConfigWithBLOBs.setId(id);
		aiClientConfigWithBLOBs.setIsActive(newState);
		WriteError.tryWrite(aiClientConfigMapper.updateByPrimaryKeySelective(aiClientConfigWithBLOBs));
		return ReturnResult.isTrue("状态更新成功", null);
	}
	
	@Override
	public ReturnResult<Map<String, Integer>> getAiMerchantType () {
		Map<String, Integer> map = Arrays.stream(
				AiMerchantType.values()).collect(
				Collectors.toMap(
						AiMerchantType::getName, AiMerchantType::getCode));
		return ReturnResult.isTrue("获取成功", map);
	}
}