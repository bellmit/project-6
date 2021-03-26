package com.miguan.ballvideo.common.aop.validated;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author shixh
 * @Date 2019/12/5
 **/
public class NoContainsValidated implements ConstraintValidator<NoContains,String> {

    private String character;

    @Override
    public void initialize(NoContains noContains) {
        character = noContains.character();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtils.isNotBlank(value)){
            return !value.contains(character);
        }
        return true;
    }
}
