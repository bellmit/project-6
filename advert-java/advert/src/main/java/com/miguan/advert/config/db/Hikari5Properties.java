package com.miguan.advert.config.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.fifthdatasource.hikari")
@Data
public class Hikari5Properties {
    private long connectionTimeout;
    private int maximumPoolSize;
    private long maxLifetime;
    private int minimumIdle;
    private long validationTimeout;
    private long idleTimeout;
}
