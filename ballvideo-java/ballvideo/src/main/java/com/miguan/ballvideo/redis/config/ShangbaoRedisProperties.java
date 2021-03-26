package com.miguan.ballvideo.redis.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class ShangbaoRedisProperties {
    private int shangbaoDatabase;
    private String shangbaoHost;
    private int shangbaoPort;
    private String shangbaoPassword;
    private int shangbaoTimeOut;
}
