package com.miguan.ballvideo.common.config.db;

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

    @Resource
    private Hikari4Properties hikari4Properties;

    @Resource
    private Hikari5Properties hikari5Properties;

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


    @Bean("fourthProperties")
    @ConfigurationProperties(prefix = "spring.fourthdatasource")
    public DataSourceProperties fourthProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "fourthDataSource")
    public HikariDataSource fourthDataSource(@Qualifier(value = "fourthProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari4Properties,ds);
        return ds;
    }

    @Bean("fiveProperties")
    @ConfigurationProperties(prefix = "spring.fivedatasource")
    public DataSourceProperties fiveProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "fiveDataSource")
    public HikariDataSource fiveDataSource(@Qualifier(value = "fiveProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari5Properties,ds);
        return ds;
    }

}
