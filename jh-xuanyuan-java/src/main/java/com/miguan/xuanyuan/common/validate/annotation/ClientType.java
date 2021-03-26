package com.miguan.xuanyuan.common.validate.annotation;

import com.miguan.xuanyuan.common.validate.realize.ClientTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClientTypeValidator.class)
public @interface ClientType {
    String message() default "该操作系统目前不支持！";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
