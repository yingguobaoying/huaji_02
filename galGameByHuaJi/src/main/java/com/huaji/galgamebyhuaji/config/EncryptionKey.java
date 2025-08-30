package com.huaji.galgamebyhuaji.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionKey {
	/**
	 * 加密密码用的密钥
	 */
	@Value("${encryption.fixed-salt}")
	private String key;

	public String getKey() {
		return key;
	}
}