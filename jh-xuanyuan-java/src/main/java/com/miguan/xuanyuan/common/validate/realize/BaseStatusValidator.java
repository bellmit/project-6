package com.miguan.xuanyuan.common.validate.realize;

import com.miguan.xuanyuan.common.property.BaseProperty;
import com.miguan.xuanyuan.common.validate.annotation.BaseStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BaseStatusValidator implements ConstraintValidator<BaseStatus, Object> {

    @Override
    public void initialize(BaseStatus constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return BaseProperty.baseStatus.contains(value);
    }
}
