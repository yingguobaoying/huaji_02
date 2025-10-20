package com.huaji.galgamebyhuaji.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Getter
@Component
@DependsOn("vaultConfigValidator")
public class EncryptionKey {
	/**
	 * 加密密码用的密钥
	 */
	@Value("${encryption.fixed-salt}")
	private String key;
	
}
