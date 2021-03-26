package com.miguan.report.common.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory4LaiDian",
        transactionManagerRef = "transactionManager4LaiDian",
        basePackages = {"com.miguan.report.repository4laidian"}
)
public class HikariLaiDianEntityManage {

    @Resource
    private HikariDataSource laiDianDataSource;

    @Autowired
    private Environment environment;

    @Bean(name = "entityManager4LaiDian", destroyMethod = "close")
    public EntityManager entityManager4LaiDian(EntityManagerFactoryBuilder builder) {
        return entityManagerFactory4Laidian(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactory4LaiDian")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory4Laidian(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = builder.dataSource(laiDianDataSource)
                .packages("com.miguan.report.entity4laidian").persistenceUnit("HikariLaiDianEntityManage").build();
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        jpaProperties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        jpaProperties.put("hibernate.connection.charSet", "utf-8");
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Bean(name = "transactionManager4LaiDian")
    public PlatformTransactionManager transactionManager4LaiDian(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactory4Laidian(builder).getObject());
    }
}
