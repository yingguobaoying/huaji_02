package com.huaji.galgamebyhuaji.vignaAiFrame.service;

import com.huaji.galgamebyhuaji.entity.AiClientConfigWithBLOBs;
import com.huaji.galgamebyhuaji.myUtil.AESEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

import static com.huaji.galgamebyhuaji.vignaAiFrame.config.VignaChatClientConfig.API_KEY_PLACEHOLDER;

@Component
@Slf4j
@RequiredArgsConstructor
@DependsOn({"vaultConfigValidator"})
public class KeyServlet {
	@Value("${spring.cloud.vault.kv.ai-path}")
	private String aiTokenPath;
	@Value("${spring.cloud.vault.kv.backend}")
	private String bastPath;
	private final VaultTemplate vaultTemplate;
	private final AESEncryptionUtil aesEncryptionUtil;
	
	public String getApiKey (AiClientConfigWithBLOBs config) {
		String apiKey = config.getApiKey();
		if ( MyStringUtil.isNull(apiKey) ) {
			return null;
		}
		if ( API_KEY_PLACEHOLDER.equalsIgnoreCase(apiKey) ) {
			log.error("配置错误!配置{}的API_KEY为占位符!", config.getId());
			return null;
		}
		if ( Boolean.TRUE.equals(config.getKeyIsVault()) ) {
			if ( MyStringUtil.isNull(config.getApiKey()) ) {
				log.error("*****配置{}读取失败,因为路径信息为空***", config.getName());
				return null;
			}
			String path = bastPath + "/data/" + aiTokenPath + "/" + config.getApiKey();
			try {
				VaultResponse response = vaultTemplate.read(path);
				if ( response == null || response.getData() == null || response.getData().isEmpty() ) {
					log.error("*****配置{}读取失败,因为路径{}下为空***", config.getName(), path);
					return null;
				}
				Map<String, Object> data = response.getData();
				Object innerData = data.get("data");
				if ( !(innerData instanceof Map) ) {
					log.error("*****配置{}读取失败,路径{}响应结构异常***", config.getName(), path);
					return null;
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> kvData = (Map<String, Object>) innerData;
				if ( kvData.isEmpty() ) {
					log.error("*****配置{}读取失败,因为路径{}下数据为空***", config.getName(), path);
					return null;
				}
				String name = kvData.keySet().iterator().next();
				if ( kvData.size() != 1 )
					log.warn("*****配置{}读取在路径{}下发现多个密钥,随机选取一个{},\n全部密钥名称{}\n***********"
							, config.getName(), path, name, kvData.keySet());
				apiKey = kvData.get(name).toString();
				if ( MyStringUtil.isNull(apiKey) ) {
					log.error("*****配置{}读取失败,因为路径{}下读取的密钥{}为空***", config.getName(), path, name);
					return null;
				}
				return apiKey;
			} catch ( Exception e ) {
				log.info("*****配置{}读取失败,失败路径{},失败原因{}***", config.getName(), path, e.getMessage());
				return null;
			}
		} else return aesEncryptionUtil.decryptValue(config.getApiKey());
	}
}