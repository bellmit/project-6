package com.miguan.ballvideo.common.config.mybatiesDB;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.miguan.ballvideo.mapper", sqlSessionFactoryRef = "dbSqlSessionFactory")
public class DataSourceConfig {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean("dbSqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // mapper的xml形式文件位置必须要配置，不然将报错：no statement （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*.xml"));
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);  //下划线转驼峰
        return bean.getObject();
    }

    @Bean("dbSqlSessionTemplate")
    public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("dbSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
