package com.miguan.xuanyuan.common.validate.realize;

import com.miguan.xuanyuan.common.property.BaseProperty;
import com.miguan.xuanyuan.common.validate.annotation.ComplexStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ComplexStatusValidator implements ConstraintValidator<ComplexStatus, Object> {

    @Override
    public void initialize(ComplexStatus constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return BaseProperty.complexStatus.contains(value);
    }
}
