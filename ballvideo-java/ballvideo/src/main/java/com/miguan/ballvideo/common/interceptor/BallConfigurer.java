package com.miguan.ballvideo.common.interceptor;

import com.miguan.ballvideo.common.interceptor.argument.AbTestAdvParamsArgumentResolver;
import com.miguan.ballvideo.common.interceptor.argument.CommonParamsArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Description 拦截器配置
 * @Author zhangbinglin
 * @Date 2019/7/10 15:53
 **/
@Configuration
public class BallConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截的管理器
        InterceptorRegistration registration = registry.addInterceptor(new BallInterceptor());     //拦截的对象会进入这个类中进行判断
        registration.addPathPatterns("/**");                    //所有路径都被拦截
        registration.excludePathPatterns("/swagger-resources","/swagger-ui.html", "/error","/webjars/**");       //添加不拦截路径
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CommonParamsArgumentResolver());
        argumentResolvers.add(new AbTestAdvParamsArgumentResolver());
    }

}
