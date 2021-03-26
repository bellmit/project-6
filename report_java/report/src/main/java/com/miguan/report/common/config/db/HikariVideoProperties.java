package com.miguan.report.common.config.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 视频数据源配置信息
 **/
@Component
@ConfigurationProperties(prefix = "spring.datasource-video.hikari")
@Data
public class HikariVideoProperties {
    private long connectionTimeout;
    private int maximumPoolSize;
    private long maxLifetime;
    private int minimumIdle;
    private long validationTimeout;
    private long idleTimeout;
}
