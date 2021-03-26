package com.miguan.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-06-22 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private int code;
    private String message;
    private T data;
}
