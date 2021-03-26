package com.miguan.idmapping.mq.dto;

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


    public SystemQueueVo(String operation) {
        this.operation = operation;
    }

    private String operation;//flashCache-更新系统缓存，
    private String desc;
}
