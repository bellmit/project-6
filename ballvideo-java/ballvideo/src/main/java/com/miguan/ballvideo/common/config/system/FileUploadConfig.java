package com.miguan.ballvideo.common.config.system;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 解决MultipartFile不能转换为CommonsMultipartFile的问题
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-13
 */
@Component
public class FileUploadConfig {
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // 20971520 - 20MB， 104857600L - 100MB
        multipartResolver.setMaxUploadSize(104857600L);
        multipartResolver.setMaxInMemorySize(1048576);
        return multipartResolver;
    }
}
