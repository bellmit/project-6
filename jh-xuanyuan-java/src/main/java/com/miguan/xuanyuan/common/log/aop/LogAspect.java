package com.miguan.xuanyuan.common.log.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.log.LogStorage;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.security.context.UserContext;
import com.miguan.xuanyuan.common.util.IpUtil;
import com.miguan.xuanyuan.common.util.ReflectUtil;
import com.miguan.xuanyuan.entity.XyOperationLog;
import com.miguan.xuanyuan.service.XyOperationLogService;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {
    @Autowired
    HttpServletRequest request;

    @Autowired
    XyOperationLogService xyOperationLogService;

    //Controller层切点
    @Pointcut("@annotation(com.miguan.xuanyuan.common.log.annotation.LogInfo)")
    public  void cutMethod() {
    }

    /**
     * 环绕通知：灵活自由的在目标方法中切入代码
     */
    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try{
            //或许需要填充的数据
            String ip = IpUtil.getIp(request);
            LogInfo logInfo = getDeclaredAnnotation(joinPoint);
            String pathName = logInfo.pathName();
            int plat = logInfo.plat();
            int type = logInfo.type();
            //创建局部变量
            LogStorage.init();
            // 获取方法传入参数
            Object[] params = joinPoint.getArgs();
            if(type == XyConstant.LOG_SAVE){
                if(params == null || params.length == 0){
                    type = XyConstant.LOG_ADD;
                } else {
                    Object id = ReflectUtil.getValue(params[0],"id");
                    type = id == null ? XyConstant.LOG_ADD : XyConstant.LOG_UPDATE;
                }
            }
            // 执行源方法
            Object proceed = joinPoint.proceed();
            XyOperationLog log = new XyOperationLog();
            if(CollectionUtils.isEmpty(LogStorage.getAll())){
                return proceed;
            }
            log.setChangeContent(JSON.toJSON(LogStorage.getAll()).toString());
            log.setDate(new Date());
            log.setIp(ip);
            log.setOperationPlat(plat);
            log.setPathName(pathName);
            log.setType(type);
            log.setUserId(UserContext.get() == null ? 1 : UserContext.get().getUserId());//todo
            xyOperationLogService.save(log);
            return proceed;
        } finally {
            LogStorage.close();
        }


    }
    /**
     * 获取方法中声明的注解
     *
     * @param joinPoint
     * @return
     * @throws NoSuchMethodException
     */
    public LogInfo getDeclaredAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        // 拿到方法定义的注解信息
        LogInfo annotation = objMethod.getDeclaredAnnotation(LogInfo.class);
        // 返回
        return annotation;
    }
}
