package com.miguan.reportview.common.exception;

import java.util.function.Supplier;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
public class ResultCheckException extends RuntimeException {


    public ResultCheckException() {
        this(()->"未找到任何数据");
    }

    public ResultCheckException(Supplier<String> message) {
        super(message.get());
    }

    public ResultCheckException(Throwable cause) {
        super(cause);
    }
}
