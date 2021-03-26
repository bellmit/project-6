package com.miguan.xuanyuan.common.log.annotation;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author kangkunhuang
 * @Description 操作日志忽略该字段
 * @Date 2021/3/9
 **/
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InsertIgnore {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
