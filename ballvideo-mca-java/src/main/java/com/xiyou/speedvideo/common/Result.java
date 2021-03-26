package com.xiyou.speedvideo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回值通用类
 * @author huangjx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {

    /**
     * 成功
     */
    private static final int SUCCESS = 1;

    /**
     * 失败
     */
    private static final int ERROR = 0;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回内容
     */
    private Object content;

    public static Result suc(String msg, Object content) {
        return new Result(1, msg, content);
    }

    public static Result suc(String msg) {
        return new Result(1, msg, null);
    }

    public static Result suc(Object content) {
        return new Result(1, "请求成功", content);
    }

    public static Result suc() {
        return new Result(1, null, null);
    }

    public static Result err(String msg, Object content) {
        return new Result(0, msg, content);
    }

    public static Result err(String msg) {
        return new Result(0, msg, null);
    }

    public static Result err() {
        return new Result(0, null, null);
    }

}
