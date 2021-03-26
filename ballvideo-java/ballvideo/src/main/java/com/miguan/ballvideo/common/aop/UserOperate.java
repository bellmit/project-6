package com.miguan.ballvideo.common.aop;

import java.lang.annotation.*;


/**
 * 记录用户操作日志
 * @author shixh
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PARAMETER})
public @interface UserOperate {

    String name() default "";//具体操作,参照OperateConstant

    String business() default "";//操作业务

    boolean recordParams() default false;//是否保存参数
}
