package com.miguan.xuanyuan.common.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityAllowAccessConfig {

    private List<String> allowAccessList;

}
