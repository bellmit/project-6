package com.miguan.xuanyuan.common.util;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.construct.LogConstruct;
import com.miguan.xuanyuan.common.log.annotation.DeleteIgnore;
import com.miguan.xuanyuan.common.log.annotation.InsertIgnore;
import com.miguan.xuanyuan.common.log.annotation.TableInfo;
import com.miguan.xuanyuan.common.log.annotation.UpdateIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.*;
import java.util.List;

public class ReflectUtil {

    /**
     * 通过字段名从对象或对象的父类中得到字段的值
     * @param object 对象实例
     * @param fieldName 字段名
     * @return 字段对应的值
     * @throws Exception
     */
    public static Object getValue(Object object, String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * @Author kangkunhuang
     * @Description 获取接口对象的类
     * @Date 2021/3/8
     **/
    public static Class proxyInterfaceClazz(Object proxy) throws Exception {
        Class<?> clazz = proxy.getClass();
        if(AopUtils.isCglibProxy(proxy)){
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            clazz = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getProxiedInterfaces()[0];
        } else if (AopUtils.isJdkDynamicProxy(proxy)){
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);
            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            clazz = ((AdvisedSupport)advised.get(aopProxy)).getProxiedInterfaces()[0];
        }
        return clazz;
    }

    /**
     * @Author kangkunhuang
     * @Description 根据类文件，获取范形类名
     * @Date 2021/3/8
     **/
    public static Type proxyInterfaceActualType(Object obj) throws Exception {
        return proxyInterfaceActualType(proxyInterfaceClazz(obj));
    }

    /**
     * @Author kangkunhuang
     * @Description 根据类文件，获取范形类名
     * @Date 2021/3/8
     **/
    public static Type proxyInterfaceActualType(Class clazz) throws Exception {
        Type[] types = clazz.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArray != null && typeArray.length>0) {
                    for (Type t : typeArray) {
                        if (t instanceof TypeVariable || t instanceof WildcardType) {
                            break;
                        } else {
                            target = (ParameterizedType) type;
                            Type[] fxTypes = target.getActualTypeArguments();
                            for (Type fxType:fxTypes) {
                                return fxType;
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    public static List<String> ignoreColumn(Class<? extends Type> clazz,Class anno) {
        if(clazz == null){
            return null;
        }
        List<String> ignoreColumns = Lists.newArrayList();
        //本类的忽略字段
        Field[] fields = clazz.getDeclaredFields();
        ignoreColumn(ignoreColumns, fields,anno);
        //直接父类的忽略字段
        Class<?> superclass = clazz.getSuperclass();
        if(superclass != null){
            Field[] superFields = superclass.getDeclaredFields();
            ignoreColumn(ignoreColumns,superFields, anno);
        }

        return ignoreColumns;
    }

    private static void ignoreColumn(List<String> ignoreColumns, Field[] fields, Class anno) {
        for (Field field:fields) {
            Object annotation = field.getAnnotation(anno);
            if(annotation != null){
                ignoreColumns.add(field.getName());
            }
        }
    }

    public static String getTableName(Type type) throws Exception{
        Class<?> clazz = (Class<?>) type;
        TableInfo tableInfoAnno = clazz.getAnnotation(TableInfo.class);
        if(tableInfoAnno != null){
            return tableInfoAnno.name();
        }
        TableName tableNameAnno = clazz.getAnnotation(TableName.class);
        if(tableNameAnno != null){
            return tableNameAnno.value();
        }
        String actualName = StringUtil.subLastPoint(type.getTypeName());
        return  StringUtil.humpToLineWithodHead(actualName);
    }

    public static List<String> ignoreColumn(Class<? extends Type> actualType, String method) {
        if(LogConstruct.UPDATE.equals(method)){
            return ignoreColumn(actualType,UpdateIgnore.class);
        } else if(LogConstruct.INSERT.equals(method)){
            return ignoreColumn(actualType,InsertIgnore.class);
        } else if(LogConstruct.DELETE.equals(method)){
            return ignoreColumn(actualType,DeleteIgnore.class);
        } else {
            return null;
        }
    }
}
