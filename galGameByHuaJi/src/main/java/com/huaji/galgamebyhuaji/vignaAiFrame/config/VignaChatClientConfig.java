package com.huaji.galgamebyhuaji.vignaAiFrame.config;

import com.huaji.galgamebyhuaji.constant.AiConstant;
import com.huaji.galgamebyhuaji.dao.AiClientConfigMapper;
import com.huaji.galgamebyhuaji.entity.AiClientConfig;
import com.huaji.galgamebyhuaji.entity.AiClientConfigExample;
import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.ListUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.vignaAiFrame.filter.MyBaseAdvisor;
import com.huaji.galgamebyhuaji.vignaAiFrame.myenum.AiMerchantType;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.KeyServlet;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.VignaHttpClient;
import com.huaji.galgamebyhuaji.vignaAiFrame.service.impl.VignaBaseClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class VignaChatClientConfig {
	private final AiClientConfigMapper clientConfigMapper;
	public static final String API_KEY_PLACEHOLDER = "红豆可爱捏";
	private static volatile Map<Long, VignaHttpClient> configMap = Collections.emptyMap();
	private static volatile Map<String, Long> idMap = Collections.emptyMap();
	private final KeyServlet keyServlet;
	private final List<MyBaseAdvisor> allAdvisor;
	
	public static VignaHttpClient getChatClient (long id) {
		return configMap.get(id);
	}
	
	public static VignaHttpClient getChatClient (String code) {
		if ( !idMap.containsKey(code) ) return null;
		return configMap.get(idMap.get(code));
	}
	
	public void info () {
		log.info("***********************开始加载AI配置项*****************************");
		AiClientConfigExample configExample = new AiClientConfigExample();
		configExample.createCriteria().andIsActiveEqualTo(true);
		List<AiClientConfigWithBLOBs> aiClientConfigs = clientConfigMapper.selectByExampleWithBLOBs(configExample);
		if ( ListUtil.isNull(aiClientConfigs) ) {
			log.info("***********************未检查到启用项目,AI配置检测完成****************************");
			return;
		}
		log.info("***********************取得启用AI配置项共:{}项****************************", aiClientConfigs.size());
		Map<Long, VignaHttpClient> map = new HashMap<>(50);
		if ( !ListUtil.isNull(allAdvisor) ) allAdvisor.sort(Comparator.comparingInt(MyBaseAdvisor::getIndex));
		for ( AiClientConfigWithBLOBs config : aiClientConfigs ) {
			Long id = config.getId();
			String code = config.getCode();
			AiMerchantType type = AiMerchantType.getByTypeNum(config.getMerchant());
			VignaHttpClient client = VignaBaseClient.getInstance(config, allAdvisor, false, type);
			client.setKeyServlet(keyServlet);//密钥授权
			map.put(id, client);
			idMap.put(code, id);
		}
	}
	
	public void refresh () {
		log.info("-----------------------重新加载AI配置项----------------------------");
		info();
		selfInspection();
		log.info("-----------------------重新加载AI配置项完成----------------------------");
	}
	
	public void selfInspection () {
		AiClientConfigExample example = new AiClientConfigExample();
		example.createCriteria().andCodeIn(AiConstant.CODE_LIST);
		List<AiClientConfig> aiClientConfigs = clientConfigMapper.selectByExample(example);
		if ( aiClientConfigs.size() != AiConstant.CODE_LIST.size() )
			log.warn("警告:实际配置的部分和预期不一致,预期配置数量{},实际为:{}", AiConstant.CODE_LIST.size(), aiClientConfigs.size());
		HashMap<String, String> codes = new HashMap<>(AiConstant.CODE_LIST.size());
		for ( AiClientConfig config : aiClientConfigs ) {
			VignaHttpClient chatClient = getChatClient(config.getId());
			if ( chatClient == null )
				log.warn("警告:配置{}初始化失败!", config.getCode());
			codes.put(config.getCode(), chatClient == null ? "失败" : "成功");
		}
		log.info("初始化完成检查,检查结果如下:");
		if ( codes.size() == AiConstant.CODE_LIST.size() ) {
			codes.forEach((k, v) -> log.info("默认配置代码:{},初始化结果:{}", k, v));
		} else {
			for ( String s : AiConstant.CODE_LIST ) {
				String r = codes.get(s);
				if ( MyStringUtil.isNull(r) )
					log.info("配置{}尚未配置", s);
				else
					log.info("默认配置代码:{},初始化结果:{}", s, r);
			}
		}
	}
}