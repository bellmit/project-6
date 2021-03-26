package com.miguan.report.common.config.db;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 广告数据源配置
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource-adv.hikari")
@Data
public class HikariAdvProperties {
    private long connectionTimeout;
    private int maximumPoolSize;
    private long maxLifetime;
    private int minimumIdle;
    private long validationTimeout;
    private long idleTimeout;
}
