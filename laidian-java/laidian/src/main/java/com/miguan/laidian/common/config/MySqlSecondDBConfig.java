package com.miguan.laidian.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * @Author shixh
 * @Date 2020/2/22
 **/
@Configuration
public class MySqlSecondDBConfig {

    @Resource
    private Hikari1Properties hikari1Properties;

    @Resource
    private Hikari2Properties hikari2Properties;

    @Resource
    private Hikari3Properties hikari3Properties;

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
        BeanUtils.copyProperties(hikari1Properties,ds);
        return ds;
    }

    @Bean("secondProperties")
    @ConfigurationProperties(prefix = "spring.seconddatasource")
    public DataSourceProperties secondProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "secodDataSource")
    public HikariDataSource secodDataSource(@Qualifier(value = "secondProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari2Properties,ds);
        return ds;
    }

    @Bean("thirdProperties")
    @ConfigurationProperties(prefix = "spring.thirddatasource")
    public DataSourceProperties thirdProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "thirdDataSource")
    public HikariDataSource thirdDataSource(@Qualifier(value = "thirdProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari3Properties,ds);
        return ds;
    }


}
