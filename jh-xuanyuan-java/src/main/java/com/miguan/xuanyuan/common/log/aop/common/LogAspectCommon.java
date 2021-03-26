package com.miguan.xuanyuan.common.log.aop.common;

import com.miguan.xuanyuan.common.construct.LogConstruct;
import com.miguan.xuanyuan.common.log.LogStorage;
import com.miguan.xuanyuan.common.util.ReflectUtil;
import com.miguan.xuanyuan.service.TableInfoService;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Type;
import java.util.List;

public class LogAspectCommon {

    private String method ;

    private TableInfoService tableInfoService;

    public LogAspectCommon(TableInfoService tableInfoService, String method){
        this.method = method;
        this.tableInfoService = tableInfoService;
    }

    public Object commonAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;
        boolean exeFlag = false;
        try{
            if(LogStorage.isEmpty()){ //无初始化,返回为空
                exeFlag = true;
                proceed = joinPoint.proceed();
                return proceed;
            }
            Object[] params = joinPoint.getArgs();
            if(params == null || params.length == 0){
                exeFlag = true;
                proceed = joinPoint.proceed();
                return proceed;
            }
            Object thisObj = joinPoint.getThis();
            String methodName = joinPoint.getSignature().getName();
            Type actualType = ReflectUtil.proxyInterfaceActualType(thisObj);
            if(actualType == null){
                exeFlag = true;
                proceed = joinPoint.proceed();
                return proceed;
            }
            List<String> ignoreColumns = ReflectUtil.ignoreColumn((Class<? extends Type>) actualType,method);
            String tableName = ReflectUtil.getTableName(actualType);
            LogConstruct construct = new LogConstruct(tableInfoService);
            //查询
            Object before = construct.findChangeInfo(thisObj, params, methodName,method,LogConstruct.DIRE_BEFORE);
            exeFlag = true;
            proceed = joinPoint.proceed();
            Object after = construct.findChangeInfo(thisObj, params, methodName,method,LogConstruct.DIRE_AFTER);
            construct.fillLog(tableName,before, after,ignoreColumns);
            return proceed;
        } catch (Exception e) {
            e.printStackTrace();
            if(!exeFlag){
                proceed = joinPoint.proceed();
            }
            return proceed;
        }
    }

}
