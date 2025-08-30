package com.huaji.galgamebyhuaji.annotation;

import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Map;

public class testNull implements ConstraintValidator<CustomNotNull, Object> {
	@Override
	public void initialize(CustomNotNull constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
		if (o == null)
			return false;
		if (o instanceof String) return !MyStringUtil.isNull(((String) o).trim());
		if (o instanceof List) return !((List<?>) o).isEmpty();
		if (o instanceof Map) return !((Map<?, ?>) o).isEmpty();
		return true;
	}

}