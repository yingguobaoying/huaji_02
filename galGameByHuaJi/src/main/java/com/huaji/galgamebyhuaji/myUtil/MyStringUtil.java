package com.huaji.galgamebyhuaji.myUtil;

import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 滑稽/因果报应
 */
public class MyStringUtil {
	public static final Pattern RFC_5322_EMAIL_PATTERN = Pattern.compile(
			"^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" +  // local part
					"@" +  // @ symbol
					"(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",  // domain part
			Pattern.CASE_INSENSITIVE
	);
	
	/**
	 * 检查并将对象中的 String 属性设置为 null（如果为空）。 请注意:此方法不会对基本字段类型:int long boolean 等进行检查
	 *
	 * @param obj 要检查的对象
	 * @param <T> 对象的类型
	 * @return 返回设置后的对象, 如果没有有效值则返回null
	 */
	@Nullable
	public static <T> T setNull(T obj) {
		if (obj == null) return null;
		
		Field[] fields = obj.getClass().getDeclaredFields();
		Method[] methods = obj.getClass().getMethods(); // public 方法，包括 setters
		boolean allIsNull = true;
		
		for (Field field : fields) {
			try {
				// 跳过原始类型（int, long, boolean 等）
				if (field.getType().isPrimitive()) continue;
				
				field.setAccessible(true);
				Object value = field.get(obj);
				if (value == null) continue;
				Class<?> fieldType = field.getType();
				String fieldName = field.getName();
				String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				boolean usedSetter = false;
				// 检查是否存在 setXXX 方法
				for (Method method : methods) {
					if (method.getName().equals(setterName)
							&& method.getParameterCount() == 1
							&& method.getParameterTypes()[0].isAssignableFrom(fieldType)) {
						if (fieldType == String.class && MyStringUtil.isNull((String) value)) {
							method.invoke(obj, (Object) null);
							usedSetter = true;
						} else if (fieldType == List.class && ((List<?>) value).isEmpty()) {
							method.invoke(obj, (Object) null);
							usedSetter = true;
						} else if (fieldType == Map.class && ((Map<?, ?>) value).isEmpty()) {
							method.invoke(obj, (Object) null);
							usedSetter = true;
						} else {
							allIsNull = false;
						}
						break; // 找到 setter 就不继续遍历了
					}
				}
				if (!usedSetter) {
					// 使用旧逻辑：直接操作字段
					if (fieldType == String.class && MyStringUtil.isNull((String) value)) {
						field.set(obj, null);
					} else if (fieldType == List.class && ((List<?>) value).isEmpty()) {
						field.set(obj, null);
					} else if (fieldType == Map.class && ((Map<?, ?>) value).isEmpty()) {
						field.set(obj, null);
					} else {
						allIsNull = false;
					}
				}
				
			} catch (Exception e) {
				MyLogUtil.error(MyStringUtil.class, e);
				e.printStackTrace();
			}
		}
		return allIsNull ? null : obj;
	}
	
	
	/**
	 * 检查字符是否非空,为空返回true
	 */
	public static boolean isNull(String str) {
		return str == null || "null".equals(str) || "NULL".equals(str) || str.isEmpty() || "NaN".equals(str) || str.trim().isEmpty();
	}
	
	public static boolean isNotNull(String str) {
		return !isNull(str);
	}
	
	/**
	 * 加密字符串,返回的是一个经过了has256加密的结果 使用的是不可逆的映射加密()
	 *
	 * @param s 需要加密的内容,如果是空字符串将返回null
	 */
	public static String encryption(String s) {
		if (MyStringUtil.isNull(s)) return null;
		try {
			s = s.trim(); // 移除首尾空白字符
			s = Normalizer.normalize(s, Normalizer.Form.NFC); // 统一为 NFC 格式
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(s.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes)
				hexString.append(String.format("%02x", b));
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			MyLogUtil.error(MyStringUtil.class, "信息加密失败!");
			MyLogUtil.error(MyStringUtil.class, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 提取字符串的字母前缀(仅包含字母部分,返回结果均为小写)
	 *
	 * @param input 完整的字符串
	 * @param size  提取的前数位(非空字符)
	 * @return 提取结果
	 */
	public static String extractPrefix(String input, int size) {
		if (isNull(input)) return "";
		if (size > input.length()) return input; // 如果要提取的长度大于字符串长度，返回原字符串
		StringBuilder result = new StringBuilder(size);
		for (int i = 0; i < input.length() && result.length() < size; i++) {
			char c = input.charAt(i);
			if (Character.isLetter(c)) {
				result.append(Character.toLowerCase(c));
			}
		}
		
		return result.toString(); // 返回有效字母
	}
	
	// 中国手机号正则表达式
	private static final String CHINA_PHONE_PATTERN = "^1[3-9]\\d{9}$";
	private static final Pattern PHONE_PATTERN = Pattern.compile(CHINA_PHONE_PATTERN);
	
	/**
	 * 验证手机号格式是否正确
	 *
	 * @param phoneNumber 要验证的手机号
	 * @return 如果手机号格式正确返回true，否则返回false
	 */
	public static boolean isValidChinesePhoneNumber(String phoneNumber) {
		if (MyStringUtil.isNull(phoneNumber)) {
			return false;
		}
		return PHONE_PATTERN.matcher(phoneNumber).matches();
	}
	
	public boolean isValidEmail(String email) {
		if (isNull(email)) {
			return false;
		}
		return RFC_5322_EMAIL_PATTERN.matcher(email).matches();
	}
}
