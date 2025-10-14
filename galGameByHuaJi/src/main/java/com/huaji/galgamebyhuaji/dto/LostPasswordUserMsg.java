package com.huaji.galgamebyhuaji.dto;

import com.huaji.galgamebyhuaji.annotation.CustomNotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LostPasswordUserMsg {
	@CustomNotNull(message = "令牌不可为空")
	@Size(min = 20, message = "令牌长度至少为20位")
	private String token;
	
	@CustomNotNull(message = "新密码不可为空")
	@Size(min = 6, message = "密码长度至少为6位")
	private String newPassword;
}
