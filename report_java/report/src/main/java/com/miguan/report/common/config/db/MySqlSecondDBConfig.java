package com.miguan.report.common.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@Configuration
public class MySqlSecondDBConfig {

    @Resource
    private HikariProperties hikariProperties;
    @Resource
    private HikariVideoProperties hikariVideoProperties;
    @Resource
    private HikariAdvProperties hikariAdvProperties;
    @Resource
    private HikariLaiDianProperties hikariLaiDIanProperties;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties(){
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikariProperties,ds);
        return ds;
    }

    @Bean("videoProperties")
    @ConfigurationProperties(prefix = "spring.datasource-video")
    public DataSourceProperties videoProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "videoDataSource")
    public HikariDataSource videoDataSource(@Qualifier(value = "videoProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikariVideoProperties, ds);
        return ds;
    }

    @Bean("advProperties")
    @ConfigurationProperties(prefix = "spring.datasource-adv")
    public DataSourceProperties advProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "advDataSource")
    public HikariDataSource advDataSource(@Qualifier(value = "advProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikariAdvProperties, ds);
        return ds;
    }

    @Bean("laiDianProperties")
    @ConfigurationProperties(prefix = "spring.datasource-laidian")
    public DataSourceProperties laiDianProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "laiDianDataSource")
    public HikariDataSource laiDianDataSource(@Qualifier(value = "laiDianProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikariLaiDIanProperties, ds);
        return ds;
    }

}
