package com.huaji.galgamebyhuaji.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = testNull.class) // 校验器
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD}) // 可用于字段和方法和参数
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomNotNull {
    String message() default "此字段不能为空"; // 校验失败时的默认错误信息

    Class<?>[] groups() default {}; // 分组

    Class<? extends Payload>[] payload() default {}; // 负载，可用于扩展
}