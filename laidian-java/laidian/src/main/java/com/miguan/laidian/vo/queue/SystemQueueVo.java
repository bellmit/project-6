package com.miguan.laidian.vo.queue;

import lombok.Data;

/**
 * 队列消息
 *
 * @Author shixh
 * @Date 2019/12/11
 **/
@Data
public class SystemQueueVo {

    public static String FLASH_CACHE = "flash_Cache";

    public static String AUTO_PUSH_CACHE = "AutoPushCache";//自动推送定时任务配置

    SystemQueueVo(){}
    public SystemQueueVo(String operation){
        this.operation = operation;
    }

    public SystemQueueVo(String operation, String desc){
        this.operation = operation;
        this.desc = desc;
    }

    private String operation;//flashCache-更新系统缓存，
    private String desc;
}
