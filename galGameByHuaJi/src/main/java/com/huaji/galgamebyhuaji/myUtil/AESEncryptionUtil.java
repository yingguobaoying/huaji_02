package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.config.EncryptionKey;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
@Lazy
public class AESEncryptionUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	/**
	 * GCM 认证标签长度（位）
	 */
	private static final int GCM_TAG_LENGTH = 128;
	/**
	 * GCM 初始化向量长度（字节）
	 */
	private static final int GCM_IV_LENGTH = 12;
	/**
	 * PBKDF2 迭代次数
	 */
	private static final int PBKDF2_ITERATIONS = 114514;
	/**
	 * AES 密钥长度（位）
	 */
	private static final int AES_KEY_LENGTH = 256;

	@Value("${encryption.aes-key}")
	private String rawKey;

	private final SecureRandom secureRandom = new SecureRandom();
	private SecretKey aesKey;
	@Autowired
	private EncryptionKey encryptionKey;

	/**
	 * 初始化方法，在 Bean 创建后自动调用
	 * 用于派生加密密钥
	 *
	 * @throws RuntimeException 如果密钥派生失败
	 */
	@PostConstruct
	public void init() {
		try {
			// 使用固定盐值派生密钥
			byte[] salt = encryptionKey.getKey().getBytes(StandardCharsets.UTF_8);
			aesKey = deriveKey(rawKey, salt);
			encryptionKey = null;
		} catch (Exception e) {
			throw new RuntimeException("密钥派生失败", e);
		}
	}

	/**
	 * 使用 PBKDF2 算法从密码和盐派生密钥
	 *
	 * @param password 密码字符串
	 * @param salt     盐值
	 * @return 派生的密钥
	 * @throws Exception 如果密钥派生过程中发生错误
	 */
	private SecretKey deriveKey(String password, byte[] salt) throws Exception {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_LENGTH);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] keyBytes = factory.generateSecret(spec).getEncoded();
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public AESEncryptionUtil() {
	}

	/**
	 * 加密字符串
	 *
	 * @param plaintext 明文字符串
	 * @return 加密后的 Base64 编码字符串
	 * @throws SecurityException 如果加密过程中发生错误
	 */
	public String encryptValue(String plaintext) {
		try {
			//初始化向量
			byte[] iv = new byte[GCM_IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
			//组合密文和向量
			byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

			return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
		} catch (Exception e) {
			throw new SecurityException("加密失败", e);
		}
	}

	/**
	 * 解密字符串
	 *
	 * @param ciphertext 加密后的 Base64 编码字符串
	 * @return 解密后的明文字符串
	 * @throws SecurityException 如果解密过程中发生错误或密文无效
	 */
	public String decryptValue(String ciphertext) {
		try {
			byte[] combined = Base64.getUrlDecoder().decode(ciphertext);
			byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
			byte[] encrypted = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new SecurityException("解密失败", e);
		}
	}
}
