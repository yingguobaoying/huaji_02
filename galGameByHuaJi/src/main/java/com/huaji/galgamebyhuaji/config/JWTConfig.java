package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

/**
 * JWT令牌配置类（生产级安全实现）
 * 支持密钥轮换、版本管理和安全增强
 */
@Component
public class JWTConfig {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA512";
	private static final int DERIVATION_ITERATIONS = 114_514;
	private static final int KEY_LENGTH = 256; // AES-256

	// 配置文件中定义的原始密钥
	@Value("${jwt.secret-key}")
	private String secretKey0; // 版本0

	@Value("${jwt.secret-key1}")
	private String secretKey1; // 版本1

	public JWTConfig() {
	}

	@Value("${jwt.secret-key2}")
	private String secretKey2; // 版本2

	@Value("${jwt.secret-key3}")
	private String secretKey3; // 版本3

	@Value("${jwt.secret-key4}")
	private String secretKey4; // 版本4

	@Value("${jwt.current-version}")
	private int currentVersion;

	@Value("${jwt.expiration-time}")
	private long expirationTime;

	// 系统盐值（增强安全性）
	@Value("${jwt.system-salt}")
	private String systemSalt;

	// 存储所有版本的标准化密钥（版本 -> 密钥）
	private final Map<Integer, byte[]> versionedKeys = new HashMap<>();

	// 当前活跃密钥列表（支持平滑轮换）
	private final List<byte[]> activeKeys = new ArrayList<>();
	@Autowired
	private PasswordEncryptionUtil p;

	/**
	 * 初始化时处理所有密钥
	 */
	@PostConstruct
	public void init() {
		// 1. 处理所有版本的密钥
		processKey(0, secretKey0);
		processKey(1, secretKey1);
		processKey(2, secretKey2);
		processKey(3, secretKey3);
		processKey(4, secretKey4);

		// 2. 设置当前活跃密钥（当前版本+前一个版本）
		setActiveKeys();

		// 3. 清理原始密钥引用（减少内存暴露）
		clearRawKeys();
	}

	/**
	 * 安全处理单个密钥
	 */
	private void processKey(int version, String rawKey) {
		if (rawKey == null || MyStringUtil.isNull(rawKey)) {
			throw new SecurityException("JWT密钥版本" + version + "未配置或为空");
		}
		// 进行标准化处理
		rawKey = Arrays.toString(p.deriveKey(rawKey));
		// 使用PBKDF2增强密钥
		byte[] normalized = deriveKey(rawKey, systemSalt.getBytes(StandardCharsets.UTF_8));
		versionedKeys.put(version, normalized);
	}

	/**
	 * 使用PBKDF2派生安全密钥（替代不安全的迭代哈希）
	 */
	private byte[] deriveKey(String rawKey, byte[] salt) {
		try {
			// 使用高成本因子增强抗暴力破解能力
			KeySpec spec = new PBEKeySpec(
					rawKey.toCharArray(),
					salt,
					DERIVATION_ITERATIONS,
					KEY_LENGTH
			);

			SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
			SecretKey secret = factory.generateSecret(spec);
			return secret.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new SecurityException("密钥派生失败", e);
		}
	}

	/**
	 * 设置当前活跃密钥（支持平滑轮换）
	 */
	private void setActiveKeys() {
		activeKeys.clear();

		// 始终包含当前版本密钥
		activeKeys.add(versionedKeys.get(currentVersion).clone());

		// 包含前一个版本密钥（支持平滑轮换）
		int previousVersion = currentVersion - 1;
		if (previousVersion >= 0 && versionedKeys.containsKey(previousVersion)) {
			activeKeys.add(versionedKeys.get(previousVersion).clone());
		}
	}

	/**
	 * 清理原始密钥字符串（减少内存暴露风险）
	 */
	private void clearRawKeys() {
		secretKey0 = null;
		secretKey1 = null;
		secretKey2 = null;
		secretKey3 = null;
		secretKey4 = null;
		System.gc(); // 提示JVM清理内存
	}

	/**
	 * 获取当前主要密钥（用于签名）
	 */
	public byte[] getCurrentSigningKey() {
		return versionedKeys.get(currentVersion).clone();
	}

	/**
	 * 获取所有活跃密钥（用于验证，支持平滑轮换）
	 */
	public List<byte[]> getActiveVerificationKeys() {
		List<byte[]> clones = new ArrayList<>();
		for (byte[] key : activeKeys) {
			clones.add(key.clone());
		}
		return clones;
	}

	/**
	 * 获取特定版本的密钥（用于特殊场景）
	 */
	public byte[] getKeyByVersion(int version) {
		if (!versionedKeys.containsKey(version)) {
			throw new IllegalArgumentException("不支持的密钥版本: " + version);
		}
		return versionedKeys.get(version).clone();
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	/**
	 * 密钥轮换通知（外部调用）
	 */
	public void rotateKey(int newVersion) {
		if (!versionedKeys.containsKey(newVersion)) {
			throw new SecurityException("无效的密钥版本: " + newVersion);
		}
		this.currentVersion = newVersion;
		setActiveKeys(); // 更新活跃密钥列表
	}

	/**
	 * 生成安全的随机盐值（用于新密钥部署）
	 */
	public static String generateSystemSalt() {
		byte[] salt = new byte[16];
		SECURE_RANDOM.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}
}