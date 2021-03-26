package com.miguan.recommend.common.config;

import com.miguan.recommend.common.interceptor.ABGroupArgumentResolver;
import com.miguan.recommend.common.interceptor.BaseDtoArgumentResolver;
import com.miguan.recommend.common.interceptor.PredictArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Description 拦截器配置
 * @Author zhangbinglin
 * @Date 2019/7/10 15:53
 **/
@Configuration
public class ArgumentResolverConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new BaseDtoArgumentResolver());
        argumentResolvers.add(new PredictArgumentResolver());
        argumentResolvers.add(new ABGroupArgumentResolver());
    }

}
