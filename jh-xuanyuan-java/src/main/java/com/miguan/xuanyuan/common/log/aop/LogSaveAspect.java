package com.miguan.xuanyuan.common.log.aop;

import com.miguan.xuanyuan.common.construct.LogConstruct;
import com.miguan.xuanyuan.common.log.aop.common.LogAspectCommon;
import com.miguan.xuanyuan.service.TableInfoService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author kangkunhuang
 * @Description 分批插入
 * @Date 2021/3/10
 **/
@Aspect
@Component
public class LogSaveAspect {

    @Autowired
    private TableInfoService tableInfoService;

    //Mapper层切点
    @Pointcut("execution (* com.miguan.xuanyuan.mapper..*.save*(..))")
    public  void cutMethod() {
    }

    /**
     * 环绕通知：灵活自由的在目标方法中切入代码
     */
    @Around("cutMethod()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        LogAspectCommon common = new LogAspectCommon(tableInfoService,LogConstruct.SAVE);
        return common.commonAspect(joinPoint);
    }
}
