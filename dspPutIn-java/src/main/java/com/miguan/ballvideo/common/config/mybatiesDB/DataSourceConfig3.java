package com.miguan.ballvideo.common.config.mybatiesDB;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.miguan.ballvideo.mapper3", sqlSessionFactoryRef = "db3SqlSessionFactory")
public class DataSourceConfig3 {

    @Autowired
    @Qualifier("threeDataSource")
    private DataSource mybaitsThreeDataSource;


    @Bean("db3SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        // 使用epsDataSource数据源, 连接业务库
        factoryBean.setDataSource(mybaitsThreeDataSource);
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources("classpath*:mappers3/*.xml");
        factoryBean.setMapperLocations(mapperLocations);
        factoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);  //下划线转驼峰
        return factoryBean.getObject();

    }

    @Bean("db3SqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("db3SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
