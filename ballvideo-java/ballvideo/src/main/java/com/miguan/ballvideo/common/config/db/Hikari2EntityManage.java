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
 * 98运营广告后台
 * @Author shixh
 * @Date 2020/4/8
 **/
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory2",
        transactionManagerRef = "transactionManager2",
        basePackages = {"com.miguan.ballvideo.repositories2"}
)
public class Hikari2EntityManage {

    @Resource
    private HikariDataSource secodDataSource;

    @Autowired
    private Environment environment;

    @Bean(name = "entityManager2",destroyMethod = "close")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder){
        return entityManageFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactory2")
    public LocalContainerEntityManagerFactoryBean entityManageFactory(EntityManagerFactoryBuilder builder){
        LocalContainerEntityManagerFactoryBean entityManagerFactory =  builder.dataSource(secodDataSource)
                .packages("com.miguan.ballvideo.entity2").persistenceUnit("Hikari2EntityManage").build();
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        jpaProperties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        jpaProperties.put("hibernate.connection.charSet", "utf-8");
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Bean(name = "transactionManager2")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManageFactory(builder).getObject());
    }
}
