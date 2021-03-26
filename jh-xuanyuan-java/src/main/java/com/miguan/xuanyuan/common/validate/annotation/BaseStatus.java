package com.miguan.xuanyuan.common.validate.annotation;

import com.miguan.xuanyuan.common.validate.realize.BaseStatusValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BaseStatusValidator.class)
public @interface BaseStatus {
    String message() default "该状态目前不支持！";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
