package com.miguan.xuanyuan.common.util;

import lombok.val;
import org.springframework.http.HttpStatus;

/**
 * 错误返回码
 *
 */
public enum ResultCode {
    //http常用状态码
    UNAUTHORIZED(401, "Unauthorized"), //未授权
    FORBIDDEN(403, "Forbidden"), //禁止访问
    NOT_FOUND(404, "Not Found"), //404
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"), //请求方法错误
    SUCCESS(0, "success"), //成功
    FAIL(400, "fail"),

    CAPTCHA_ERROR(10001, "CAPTCHA_ERROR"), //图片验证失败

    //SDK错误码
    SDK_PARAMS_ERROR(700, "缺少必填的认证信息"),
    SDK_TIME_EXPIRED(701, "该时间戳已超时"),
    SDK_AUTH_ERROR(702, "校验失败"),
    SDK_APP_NOT_EXIST(703, "该应用不存在"),
    SDK_PT_PARAMS_ERROR(703, "参数错误"),

    REPORT_SHARE_SWITCH_ERROR(20001, "SHARE_SWITCH_ERROR"), //不允许分享
    ;






    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private int code;
    private String msg;
}