package com.miguan.ballvideo.common.aop;


import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.OperateConstant;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.OperateLog;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/** 接口日志记录*/
@Slf4j
@Aspect
@Component
public class UserOperateAspect {

	private ThreadLocal<OperateLog> operateLogThreadLocal = new ThreadLocal<>();

	@Resource
	private RabbitTemplate rabbitTemplate;

    @Before("@annotation(userOperate)")
    public void doBefore(JoinPoint joinPoint, UserOperate userOperate){
		try{
			OperateLog operateLog = new OperateLog();
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			String deviceId = request.getParameter("deviceId");//公共字段
			if(userOperate.recordParams() && joinPoint.getArgs().length>0){
        		operateLog.setArgs(Arrays.toString(joinPoint.getArgs()));
			}
			operateLog.setDeviceId(deviceId);
			operateLog.setOperate(userOperate.name());
			operateLog.setIp(IPUtils.getRequestIp(request));
			operateLog.setUrl(request.getRequestURL().toString());
			operateLog.setOperateBusiness(userOperate.business());
			operateLogThreadLocal.set(operateLog);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
    }

    @AfterReturning(pointcut = "@annotation(userOperate)",returning = "object")
    public void doAfterReturing(Object object, UserOperate userOperate){
		try {
			OperateLog operateLog = operateLogThreadLocal.get();
			//目前只有新老用户判断接口需要记录，以后新业务再拓展 add shixh1223
			if(object instanceof ResultMap) {
				ResultMap resultMap = (ResultMap)object;
				if(OperateConstant.gatherSearch.equals(userOperate.name())
						&& 200==resultMap.getCode()){
					operateLog.setCode(resultMap.getCode());
					//operateLog.setOperateResult(resultMap.getData()+"");
					operateLog.setCreateDate(new Date());
					String jsonStr = JSON.toJSONString(operateLog);
					rabbitTemplate.convertAndSend(
							RabbitMQConstant.OPERATE_LOG_EXCHANGE,
							RabbitMQConstant.OPERATE_LOG_KEY,jsonStr);
				}
			}/*else if(object instanceof Map){
				if(OperateConstant.huaweiCallBack.equals(userOperate.name())){
					operateLog.setCode(200);
					operateLog.setOperateResult("success");
					operateLog.setCreateDate(new Date());
					String jsonStr = JSON.toJSONString(operateLog);
					rabbitTemplate.convertAndSend(
							RabbitMQConstant.OPERATE_LOG_EXCHANGE,
							RabbitMQConstant.OPERATE_LOG_KEY,jsonStr);
				}
			}*/
		} catch(Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			operateLogThreadLocal.remove();
		}
    }
 
}
