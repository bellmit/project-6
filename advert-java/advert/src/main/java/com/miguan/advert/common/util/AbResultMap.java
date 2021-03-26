package com.miguan.advert.common.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用返回结果类.
 *
 * @author zhicong.lin
 * @date 2019/6/19
 */
@Setter
@Getter
@ApiModel("返回结果")
@Slf4j
public final class AbResultMap<T extends Object> {

    @ApiModelProperty("编号")
    private int code;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("提示信息")
    private String msg;

    public AbResultMap() {
        this.code = 200;
        this.msg = "请求成功";
    }

    public AbResultMap(T data) {
        this();
        this.data = data;
    }

    public static <T> AbResultMap<T> success(T data) {
        return new AbResultMap<>(data);
    }

    public static <T> AbResultMap<T> success(T data, String msg) {
        AbResultMap<T> success = new AbResultMap<>(data);
        success.setCode(200);
        success.setMsg(msg);
        return success;
    }

    public static <T> AbResultMap<T> error(T data) {
        final AbResultMap<T> error = error();
        error.setData(data);
        return error;
    }

    public static <T> AbResultMap<T> error(String data) {
        final AbResultMap<T> error = error();
        error.setCode(-1);
        error.setMsg(data);
        return error;
    }

    public static <T> AbResultMap<T> error(int code, String message) {
        final AbResultMap<T> error = error();
        error.setCode(code);
        error.setMsg(message);
        return error;
    }

    public static <T> AbResultMap<T> error(T data, String message) {
        final AbResultMap<T> error = error();
        error.setData(data);
        error.setMsg(message);
        return error;
    }

    public static <T> AbResultMap<T> success() {
        return new AbResultMap<>();
    }

    public static <T> AbResultMap<T> error() {
        final AbResultMap<T> error = new AbResultMap<>();
        error.setCode(-1);
        error.setMsg("请求失败");
        return error;
    }

}
