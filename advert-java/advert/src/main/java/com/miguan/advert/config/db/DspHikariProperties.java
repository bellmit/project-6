package com.miguan.advert.config.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.dspdatasource.hikari")
@Data
public class DspHikariProperties {
    private long connectionTimeout;
    private int maximumPoolSize;
    private long maxLifetime;
    private int minimumIdle;
    private long validationTimeout;
    private long idleTimeout;
}
