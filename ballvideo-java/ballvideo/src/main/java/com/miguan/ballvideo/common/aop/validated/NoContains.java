package com.miguan.ballvideo.common.aop.validated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 *  校验-不能包含指定字符串
 * @Author shixh
 * @Date 2019/12/5
 **/
@Constraint(validatedBy = { NoContainsValidated.class })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface NoContains {
    String message() default "包含错误字符";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String character();
}
