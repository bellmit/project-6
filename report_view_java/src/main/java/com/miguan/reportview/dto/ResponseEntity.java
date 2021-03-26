package com.miguan.reportview.dto;

import com.cgcg.base.language.Translator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@ApiModel("返回结果")
@Setter
@Getter
public class ResponseEntity<T> {

    @ApiModelProperty("编号")
    private int code;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("提示信息")
    private String message;

    public ResponseEntity() {
        this.code = 200;
        this.message = Translator.toLocale("success", "操作成功");
    }

    public ResponseEntity(T data) {
        this();
        this.data = data;
    }

    public static <T> ResponseEntity success() {
        return new ResponseEntity();
    }
    public static <T> ResponseEntity success(T data) {
        return new ResponseEntity(data);
    }

    public static ResponseEntity error(int code, String message) {
        ResponseEntity error = error();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public static ResponseEntity error() {
        ResponseEntity error = new ResponseEntity();
        error.setCode(400);
        error.setMessage(Translator.toLocale("failed", "操作失败"));
        return error;
    }

}
