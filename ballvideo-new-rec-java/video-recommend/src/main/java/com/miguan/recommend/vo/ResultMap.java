package com.miguan.recommend.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用返回结果类.
 */
@Data
@Slf4j
public final class ResultMap<T extends Object> {

    private int code;
    private T data;
    private String message;

    public ResultMap() {
        this.code = 200;
        this.message = "操作成功";
    }

    public ResultMap(T data) {
        this();
        this.data = data;
    }

    public static <T> ResultMap<T> success(T data) {
        return new ResultMap<>(data);
    }

    public static <T> ResultMap<T> success(T data, String message) {
        ResultMap<T> success = new ResultMap<>(data);
        success.setCode(200);
        success.setMessage(message);
        return success;
    }

    public static <T> ResultMap<T> error(T data) {
        final ResultMap<T> error = error();
        error.setData(data);
        return error;
    }

    public static <T> ResultMap<T> error(String data) {
        final ResultMap<T> error = error();
        error.setCode(400);
        error.setMessage(data);
        return error;
    }

    public static <T> ResultMap<T> error(int code, String message) {
        final ResultMap<T> error = error();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public static <T> ResultMap<T> error(T data, String message) {
        final ResultMap<T> error = error();
        error.setData(data);
        error.setMessage(message);
        return error;
    }

    public static <T> ResultMap<T> success() {
        return new ResultMap<>();
    }

    public static <T> ResultMap<T> error() {
        final ResultMap<T> error = new ResultMap<>();
        error.setCode(400);
        error.setMessage("操作失败");
        return error;
    }

}
