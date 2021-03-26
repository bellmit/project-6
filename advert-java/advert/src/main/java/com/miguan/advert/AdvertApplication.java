package com.miguan.advert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.miguan.advert"})
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
@MapperScan("com.miguan.advert.domain.mapper")
@EnableJpaRepositories(basePackages = "com.miguan.advert.domain.repositories")
public class AdvertApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(AdvertApplication.class, args);
    }

}
