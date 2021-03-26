package com.miguan.laidian.common.interceptor;

import com.miguan.laidian.common.util.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @Author shixh
 * @Date 2019/9/18
 **/
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResultMap runtimeExceptionHandler(MaxUploadSizeExceededException e) {
        logger.error("MaxUploadSizeExceededException error："+ e.getMessage());
        long max = e.getMaxUploadSize();
        return ResultMap.error("超过上传最大限制："+max/(1024*1024)+"m");
    }
}
