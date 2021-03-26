package com.miguan.xuanyuan.common.interceptor;

import com.miguan.xuanyuan.service.XyAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


/**
 * @Description 拦截器配置
 * @Author zhangbinglin
 * @Date 2019/7/10 15:53
 **/
@Configuration
public class SDKConfigurer implements WebMvcConfigurer {

    @Resource
    private XyAppService service;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration commonregistration = registry.addInterceptor(new CommonInterceptor());
        commonregistration.addPathPatterns("/api/**");

        //登录拦截的管理器
        InterceptorRegistration registration = registry.addInterceptor(new SDKInterceptor(service));     //拦截的对象会进入这个类中进行判断
        registration.addPathPatterns("/api/sdk/strategy/**");                    //所有路径都被拦截
        registration.excludePathPatterns("/swagger-resources","/swagger-ui.html", "/error","/webjars/**");       //添加不拦截路径

    }

}
