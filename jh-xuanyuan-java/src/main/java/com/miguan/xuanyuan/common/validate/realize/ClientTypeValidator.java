package com.miguan.xuanyuan.common.validate.realize;

import com.miguan.xuanyuan.common.property.BaseProperty;
import com.miguan.xuanyuan.common.validate.annotation.ClientType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClientTypeValidator implements ConstraintValidator<ClientType, Object> {

    @Override
    public void initialize(ClientType constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return BaseProperty.ClientType.contains(value);
    }
}
