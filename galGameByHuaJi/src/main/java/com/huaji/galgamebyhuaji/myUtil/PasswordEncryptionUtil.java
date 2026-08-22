package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.config.EncryptionKey;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.Base64;

/**
 * 加密工具类，提供密码哈希生成和验证功能。
 * 使用PBKDF2WithHmacSHA256算法结合随机盐和固定盐进行密码加密，
 * 防止彩虹表攻击并增强安全性。
 */
@Component
public class PasswordEncryptionUtil {
	/**
	 * 随机盐的长度（字节数）
	 */
	private static final int SALT_LENGTH = 32;
	
	/**
	 * PBKDF2算法的迭代次数
	 */
	private static final int ITERATIONS = 114514;
	
	/**
	 * 生成的哈希值的长度（bit）
	 */
	private static final int KEY_LENGTH = 256;
	
	/**
	 * 固定盐值
	 */
	private final EncryptionKey encryptionProperties;
	
	public PasswordEncryptionUtil (EncryptionKey encryptionProperties) {this.encryptionProperties = encryptionProperties;}
	
	
	/**
	 * 生成随机盐值。
	 *
	 * @return 包含随机盐值的字节数组，长度为SALT_LENGTH
	 */
	public static byte[] generateSalt () {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[ SALT_LENGTH ];
		random.nextBytes(salt);
		return salt;
	}
	
	/**
	 * 对密码进行加密处理。
	 *
	 * @param password 需要加密的密码字符串
	 *
	 * @return 格式为"Base64(随机盐):Base64(哈希值)"的字符串，若输入密码为空则返回null
	 */
	@Nullable
	public String hashPassword (String password) {
		if ( MyStringUtil.isNull(password) ) return null;
		password = Normalizer.normalize(password, Normalizer.Form.NFC); // 统一为 NFC 格式
		return hashPassword(password.toCharArray(), null);
	}
	
	/**
	 * 使用PBKDF2算法对密码进行哈希处理。
	 * <p>
	 * 若未提供随机盐，则自动生成随机盐。
	 *
	 * @param password   要哈希的密码字符数组
	 * @param randomSalt 随机盐值字节数组（可为null）
	 *
	 * @return 格式为"Base64(随机盐):Base64(哈希值)"的字符串
	 *
	 * @throws RuntimeException 当加密过程中发生错误时抛出
	 */
	public String hashPassword (char[] password, @Nullable byte[] randomSalt) {
		try {
			if ( randomSalt == null ) {
				randomSalt = generateSalt();
			}
			PBEKeySpec spec = new PBEKeySpec(password, getSalt(randomSalt), ITERATIONS, KEY_LENGTH);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(randomSalt) + ":" + Base64.getEncoder().encodeToString(hash);
		} catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
			MyLogUtil.error(PasswordEncryptionUtil.class, "密码加密失败!");
			MyLogUtil.error(PasswordEncryptionUtil.class, e);
			throw new RuntimeException("密码加密失败", e);
		}
	}
	
	/**
	 * 将固定盐和随机盐组合生成最终盐值。
	 *
	 * @param salt 随机盐值字节数组
	 *
	 * @return 组合后的盐值字节数组（固定盐 + 随机盐）
	 */
	private byte[] getSalt (byte[] salt) {
		byte[] fixedSalt = encryptionProperties.getKey().getBytes(StandardCharsets.UTF_8);
		byte[] combinedSalt = new byte[ fixedSalt.length + salt.length ];
		System.arraycopy(fixedSalt, 0, combinedSalt, 0, fixedSalt.length);
		System.arraycopy(salt, 0, combinedSalt, fixedSalt.length, salt.length);
		return combinedSalt;
	}
	
	/**
	 * 验证输入的密码是否与存储的哈希值匹配。
	 *
	 * @param inputPassword 待验证的密码字符串
	 * @param storedValue   存储的哈希值（格式为"Base64(随机盐):Base64(哈希值)"）
	 *
	 * @return 若密码匹配返回true，输入为空或格式错误返回false
	 */
	public boolean verifyPassword (String inputPassword, String storedValue) {
		if ( MyStringUtil.isNull(inputPassword) || MyStringUtil.isNull(storedValue) ) return false;
		inputPassword = Normalizer.normalize(inputPassword, Normalizer.Form.NFC);
		return verifyPassword(inputPassword.toCharArray(), storedValue);
	}
	
	/**
	 * 验证输入的密码字符数组是否与存储的哈希值匹配。
	 * <p>
	 * 使用恒定时间比较防止时序攻击。
	 *
	 * @param inputPassword 待验证的密码字符数组
	 * @param storedValue   存储的哈希值（格式为"Base64(随机盐):Base64(哈希值)"）
	 *
	 * @return 若密码匹配返回true，格式错误或异常时返回false
	 */
	private boolean verifyPassword (char[] inputPassword, String storedValue) {
		try {
			String[] parts = storedValue.split(":");
			if ( parts.length != 2 ) return false;
			
			byte[] randomSalt = Base64.getDecoder().decode(parts[ 0 ]);
			byte[] storedHash = Base64.getDecoder().decode(parts[ 1 ]);
			
			PBEKeySpec spec = new PBEKeySpec(inputPassword, getSalt(randomSalt), ITERATIONS, KEY_LENGTH);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] inputHash = skf.generateSecret(spec).getEncoded();
			// 添加少量随机延迟：防止微小时间差被观察
			applyRandomDelay(10, 100);
			return MessageDigest.isEqual(storedHash, inputHash);
		} catch ( Exception e ) {
			MyLogUtil.error(PasswordEncryptionUtil.class, "密码验证失败!");
			MyLogUtil.error(PasswordEncryptionUtil.class, e);
			return false;
		}
	}
	
	/**
	 * 在指定范围内添加随机延迟，用于扰乱时序攻击。
	 *
	 * @param minMillis 最小延迟（毫秒）
	 * @param maxMillis 最大延迟（毫秒）
	 */
	public void applyRandomDelay (int minMillis, int maxMillis) {
		if ( minMillis < 0 || maxMillis < minMillis ) return;
		
		int delay = new SecureRandom().nextInt(maxMillis - minMillis + 1) + minMillis;
		try {
			Thread.sleep(delay);
		} catch ( InterruptedException ignored ) {
			Thread.currentThread().interrupt(); // 恢复中断状态
		}
	}
	
	/**
	 * 使用 PBKDF2 算法从原始密钥派生出标准密钥（用于加密等场景）
	 * 如果你实在想使用发癫密钥请先用这玩意派生一次
	 *
	 * @param rawKey 原始密钥字符串
	 *
	 * @return 派生后的密钥字节数组
	 */
	public byte[] deriveKey (String rawKey) {
		try {
			byte[] salt = encryptionProperties.getKey().getBytes(StandardCharsets.UTF_8); // 固定盐
			PBEKeySpec spec = new PBEKeySpec(rawKey.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			return factory.generateSecret(spec).getEncoded();
		} catch ( Exception e ) {
			throw new RuntimeException("密钥派生失败", e);
		}
	}
	
}