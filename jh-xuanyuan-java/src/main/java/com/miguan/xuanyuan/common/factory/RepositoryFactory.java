package com.miguan.xuanyuan.common.factory;

import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public  class RepositoryFactory {
    private static final RepositoryFactory repositoryFactory = new RepositoryFactory();

    private RepositoryFactory(){};

    public static RepositoryFactory getInstance(){
        return repositoryFactory;
    }

    public static Method getMethod(Class<?> clazz,String methodName,Class<?>... clazzes){
        return ReflectionUtils.findMethod(clazz, methodName, clazzes);
    }

    public static Object invoke(Method method, @Nullable Object target, @Nullable Object... args){
        return ReflectionUtils.invokeMethod(method, target, args);
    }
}
