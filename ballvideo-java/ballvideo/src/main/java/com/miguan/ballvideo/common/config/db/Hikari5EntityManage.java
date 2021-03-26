package com.miguan.ballvideo.common.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.util.Properties;

/**
 * 任务系统数据库
 * @Author laiyd
 * @Date 2020/7/13
 **/
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory5",
        transactionManagerRef = "transactionManager5",
        basePackages = {"com.miguan.ballvideo.repositories5"}
)
public class Hikari5EntityManage {

    @Resource(name = "fiveDataSource")
    private HikariDataSource fiveDataSource;

    @Autowired
    private Environment environment;

    @Bean(name = "entityManager5",destroyMethod = "close")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder){
        return entityManageFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactory5")
    public LocalContainerEntityManagerFactoryBean entityManageFactory(EntityManagerFactoryBuilder builder){
        LocalContainerEntityManagerFactoryBean entityManagerFactory =  builder.dataSource(fiveDataSource)
                .packages("com.miguan.ballvideo.entity5").persistenceUnit("Hikari5EntityManage").build();
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        jpaProperties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        jpaProperties.put("hibernate.connection.charSet", "utf-8");
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Bean(name = "transactionManager5")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManageFactory(builder).getObject());
    }
}
