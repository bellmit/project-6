package com.miguan.flow.common.config;

import com.miguan.flow.common.aop.AbTestAdvParamsArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Description 拦截器配置
 **/
@Configuration
public class BallConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AbTestAdvParamsArgumentResolver());
    }

}
