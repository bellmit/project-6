package com.miguan.laidian.common.config;

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
        multipartResolver.setMaxUploadSize(104857600);
        multipartResolver.setMaxInMemorySize(1048576);
        return multipartResolver;
    }
}
