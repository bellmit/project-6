package com.miguan.recommend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.service.predict")
public class PredictConfig {

    private String predictApi;

    private String predictApi3;

    private String predictTranApi;

    private Double lambda;
}
