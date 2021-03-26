package com.miguan.recommend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.es")
public class EsConfig {

    private String host;
    private int port;
    private String scheme;
    private String video_embedding;
    private String user_embedding;
    private String video_title;
}
