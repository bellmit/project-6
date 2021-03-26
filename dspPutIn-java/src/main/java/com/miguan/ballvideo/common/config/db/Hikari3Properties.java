package com.miguan.ballvideo.common.config.db;

/**dsp自投
 * @Author suhj
 * @Date 2020/9/2
 **/

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.threedatasource.hikari")
@Data
public class Hikari3Properties {
    private long connectionTimeout;
    private int maximumPoolSize;
    private long maxLifetime;
    private int minimumIdle;
    private long validationTimeout;
    private long idleTimeout;
}
