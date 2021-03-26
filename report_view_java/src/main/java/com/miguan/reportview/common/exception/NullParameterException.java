package com.miguan.reportview.common.exception;

import java.util.function.Supplier;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
public class NullParameterException extends RuntimeException {

    public NullParameterException() {
        this(()->"请求参数不为空");
    }

    public NullParameterException(Supplier<String> message) {
        super(message.get());
    }
}
