package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 最高优先级，最先初始化
public class VaultConfigValidator {
	
	private final Environment environment;
	
	public VaultConfigValidator(Environment environment) {
		this.environment = environment;
	}
	
	@PostConstruct
	public void validateVaultConfig() {
		System.out.println("=== 开始验证Vault配置加载 ===");
		
		// 检查关键配置是否已从Vault加载
		checkConfig("jwt.secret-key", "JWT密钥加载成功");
		checkConfig("jwt.secret-key1", "JWT密钥1加载成功");
		checkConfig("jwt.secret-key2", "JWT密钥2加载成功");
		checkConfig("jwt.secret-key3", "JWT密钥3加载成功");
		checkConfig("jwt.secret-key4", "JWT密钥4加载成功");
		checkConfig("jwt.system-salt", "JWT系统盐值加载成功");
		checkConfig("encryption.fixed-salt", "加密固定盐加载成功");
		checkConfig("encryption.aes-key", "AES密钥加载成功");
		checkConfig("mail.smtp-authorization-code", "邮件授权码加载成功");
		checkConfig("datasource.password", "数据库密码加载成功");
		
		System.out.println("=== Vault配置验证完成 ===");
	}
	
	private void checkConfig(String key, String description) {
		String value = environment.getProperty(key);
		if (MyStringUtil.isNull(value)) {
			throw new IllegalStateException(description + "未从Vault加载，key: " + key);
		}
		System.out.println(description + " ✓ (长度: " + value.length() + ")");
	}
}
