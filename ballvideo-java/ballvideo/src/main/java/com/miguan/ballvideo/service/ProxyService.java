package com.miguan.ballvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

/**
 * 通过代理对象解决内部类调用也能进行切面
 * example:子类实现setSelf
 *      @Override
 *      protected void setSelf() {
 *          proxyObject = context.getBean(ClMenuConfigService.class);
 *     }
 *     内部类调用改成 proxyObject.method

 * @Author shixh
 * @Date 2020/1/3
 **/
public abstract class ProxyService<T> {
    @Autowired
    protected ApplicationContext context;
    protected T proxyObject;

    @PostConstruct
    protected abstract void setSelf();
}
