package com.miguan.xuanyuan.common.log.annotation;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableInfo {

    String name() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
