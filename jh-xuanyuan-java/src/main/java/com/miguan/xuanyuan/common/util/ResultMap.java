package com.miguan.xuanyuan.common.util;

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
public final class ResultMap<T> {

    @ApiModelProperty("编号")
    private int code;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("提示信息")
    private String message;

    public ResultMap() {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMsg();
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
        success.setCode(ResultCode.SUCCESS.getCode());
        success.setMessage(message);
        return success;
    }

    public static <T> ResultMap<T> error(T data) {
        final ResultMap<T> error = error();
        error.setData(data);
        return error;
    }

    public static <T> ResultMap<T> error(String message) {
        final ResultMap<T> error = error();
        error.setCode(ResultCode.FAIL.getCode());
        error.setMessage(message);
        return error;
    }

    /**
     * @Description 错误验证，验证码401
     * @Date 2021/1/21
     **/
    public static <T> ResultMap<T> errorValidate(String data) {
        final ResultMap<T> error = error();
        error.setCode(401);
        error.setMessage(data);
        return error;
    }

    public static <T> ResultMap<T> error(ResultCode resultCode) {
        final ResultMap<T> error = error();
        error.setCode(resultCode.getCode());
        error.setMessage(resultCode.getMsg());
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
        error.setCode(ResultCode.FAIL.getCode());
        error.setMessage(ResultCode.FAIL.getMsg());
        return error;
    }

}
