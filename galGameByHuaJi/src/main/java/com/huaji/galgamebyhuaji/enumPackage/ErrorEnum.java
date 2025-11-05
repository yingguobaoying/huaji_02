package com.huaji.galgamebyhuaji.enumPackage;


import lombok.Getter;

/**
 * 这个类禁止格式化!!!!!!千万补药啊!!!!!!! 这个类禁止格式化!!!!!!千万补药啊!!!!!!! 这个类禁止格式化!!!!!!千万补药啊!!!!!!! 错误码枚举类
 * <p>
 * 错误码格式说明：错误类型(1位) + 错误编号(1位)
 *
 * <table border="1">
 *   <caption>错误码速查表</caption>
 *   <tr>
 *     <th>错误类型</th>
 *     <th>错误编号</th>
 *     <th>枚举名称</th>
 *     <th>描述信息</th>
 *   </tr>
 * <p>
 *   <tr>全局错误 </tr>
 *   <tr><td>0</td><td>0</td><td>UNKNOWN_ERROR</td><td>未知错误</td></tr>
 *   <tr><td>0</td><td>1</td><td>UNKNOWN_ERROR_NUM</td><td>未知错误编号</td></tr>
 * <p>
 *   <tr> 用户相关错误 (1xxx)</tr>
 *   <tr><td>1</td><td>0</td><td>USER_ERROR</td><td>未知用户错误类型</td></tr>
 *   <tr><td>1</td><td>1</td><td>USER_REPEAT_ERROR</td><td>用户重复错误</td></tr>
 *   <tr><td>1</td><td>2</td><td>USER_NOT_LOGIN_ERROR</td><td>用户未登录</td></tr>
 * <p>
 *   <tr> 会话相关错误 (2xxx) </tr>
 *   <tr><td>2</td><td>0</td><td>SESSION_ERROR</td><td>未知会话错误类型</td></tr>
 *   <tr><td>2</td><td>1</td><td>SESSION_REPEAT_ERROR</td><td>会话重复错误</td></tr>
 *   <tr><td>2</td><td>2</td><td>SESSION_DIFFERENT_ERROR</td><td>异地登录会话</td></tr>
 *   <tr><td>2</td><td>3</td><td>SESSION_NOT_AVAILABLE_ERROR</td><td>会话不存在</td></tr>
 *   <tr><td>2</td><td>4</td><td>SESSION_TOKEN_ERROR</td><td>会话Token不匹配或者错误</td></tr>
 *   <tr><td>2</td><td>5</td><td>SESSION_IP_CHANGED</td><td>会话ID校验失败</td></tr>
 *   <tr><td>2</td><td>6</td><td>SESSION_DEVICE_CHANGED</td><td>设备校验失败</td></tr>
 * <p>
 *   <tr>数据库相关错误 (3xxx)</tr>
 *   <tr><td>3</td><td>0</td><td>WRITE_ERROR</td><td>未知数据库写入错误</td></tr>
 *   <tr><td>3</td><td>1</td><td>WRITE_DIU_ERROR</td><td>数据库更新错误</td></tr>
 * </table>
 * <p>
 */
@Getter
public enum ErrorEnum {
	// @formatter:off
	//枚举名称***********************错误类型********错误编号*******描述信息*************************
	UNKNOWN_ERROR               (0, 0, "未知错误"),
	UNKNOWN_ERROR_NUM           (0, 1, "未知错误编号"),
	USER_ERROR                  (1, 0, "未知用户错误类型"),
	USER_REPEAT_ERROR           (1, 1, "用户重复错误"),
	SESSION_ERROR               (2, 0, "未知会话错误类型"),
	SESSION_REPEAT_ERROR        (2, 1, "会话重复错误"),
	SESSION_DIFFERENT_ERROR     (2, 2, "异地登录会话"),
	SESSION_NOT_AVAILABLE_ERROR (2, 3, "会话不存在"),
	SESSION_TOKEN_ERROR         (2, 4, "会话Token不匹配或者错误"),
	SESSION_IP_CHANGED          (2, 5, "会话ID校验失败!"),
	SESSION_DEVICE_CHANGED      (2, 6, "设备校验失败!"),
	SESSION_OVERDUE             (2, 7, "会话过期"),
	WRITE_ERROR                 (3, 0, "未知数据库写入错误"),
	WRITE_DIU_ERROR             (3, 1, "数据库写入错误"),
		;
	// @formatter:on
	ErrorEnum(int error_type, int error_num, String error_msg) {
		this.error_type = error_type;
		this.error_num = error_num;
		this.error_msg = error_msg;
	}
	
	private final int error_type;
	private final int error_num;
	private final String error_msg;
	
	/**
	 * 智能匹配错误枚举
	 *
	 * @param errorType 错误类型
	 * @param errorNum  错误编号
	 *
	 * @return 匹配的枚举项，匹配优先级： 1. 完全匹配(error_type和error_num) 2. 匹配类型+默认编号0 3. 全局UNKNOWN_ERROR
	 */
	public static ErrorEnum getError(int errorType, int errorNum) {
		// 优先尝试完全匹配
		for (ErrorEnum error : values()) {
			if (error.error_type == errorType && error.error_num == errorNum) {
				return error;
			}
		}
		
		// 次优匹配：同类型的默认错误（编号为0）
		for (ErrorEnum error : values()) {
			if (error.error_type == errorType && error.error_num == 0) {
				return error;
			}
		}
		
		// 最后返回全局未知错误
		return UNKNOWN_ERROR;
	}
}
