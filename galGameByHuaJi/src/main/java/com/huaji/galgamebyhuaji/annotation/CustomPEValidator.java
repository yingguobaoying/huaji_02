package com.huaji.galgamebyhuaji.annotation;

import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomPEValidator implements ConstraintValidator<CustomPE, Object> {
	@Override
	public void initialize (CustomPE constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
	
	@Override
	public boolean isValid (Object o, ConstraintValidatorContext context) {
		switch ( o ) {
			case null -> {
				return true;
			}
			case String s -> {
				if ( MyStringUtil.isNull(s) )//由于手机号为非必须字段,这里就直接略过手机号检查,才不是因为穷只能依靠邮箱验证
					return true;
				return MyStringUtil.isValidChinesePhoneNumber(s);
			}
			
			//其它类型时
			case Integer i -> {
				return true;
			}
			case Long l -> {
				return true;
			}
			default -> {
				return false;
			}
		}
	}
	
}