package com.miguan.xuanyuan.common.log.annotation;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInfo {

    String pathName() default "";

    int plat() default 1; // 1 前台,2 后台

    int type() default 1;  // 1 新增,2 修改,3 删除,4 新增或者修改

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
