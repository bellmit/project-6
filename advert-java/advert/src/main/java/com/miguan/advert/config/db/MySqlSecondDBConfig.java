package com.miguan.advert.config.db;

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
    private Hikari3Properties hikari3Properties;

    @Resource
    private Hikari5Properties hikari5Properties;

    @Resource
    private DspHikariProperties dspHikariProperties;

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

    @Bean("thirdProperties")
    @ConfigurationProperties(prefix = "spring.thirddatasource")
    public DataSourceProperties thirdProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "thirddatasource")
    public HikariDataSource thirddatasource(@Qualifier(value = "thirdProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari3Properties,ds);
        return ds;
    }

    @Bean("fifthProperties")
    @ConfigurationProperties(prefix = "spring.fifthdatasource")
    public DataSourceProperties fifthProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "fifthdatasource")
    public HikariDataSource fifthDataSource(@Qualifier(value = "fifthProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(hikari5Properties,ds);
        return ds;
    }

    @Bean("dspProperties")
    @ConfigurationProperties(prefix = "spring.dspdatasource")
    public DataSourceProperties dspProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "dspDatasource")
    public HikariDataSource dspDatasource(@Qualifier(value = "dspProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
        BeanUtils.copyProperties(dspHikariProperties,ds);
        return ds;
    }

}
