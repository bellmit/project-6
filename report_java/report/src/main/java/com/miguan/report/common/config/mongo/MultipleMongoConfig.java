package com.miguan.report.common.config.mongo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import javax.annotation.Resource;

/**
 * @author zhongli
 * @date 2020-06-28 
 *
 */
@Configuration
@EnableConfigurationProperties(MultipleMongoProperties.class)
public class MultipleMongoConfig {
    @Resource
    private MultipleMongoProperties multipleMongoProperties;

    @Bean(name = PrimaryMongoConfig.MONGO_TEMPLATE)
    @Primary
    public MongoTemplate primaryMongoTeamplate(@Qualifier("primaryMongoDatabaseFactory") ObjectProvider<MongoDatabaseFactory> mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory.getIfAvailable());
    }

    @Primary
    @Bean(name = "primaryMongoDatabaseFactory")
    public MongoDatabaseFactory primaryMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(multipleMongoProperties.getPrimary().getUri());
    }

    @Bean(name = SecondMongoConfig.MONGO_TEMPLATE)
    @Primary
    public MongoTemplate secondMongoTeamplate(@Qualifier("secondMongoDatabaseFactory") ObjectProvider<MongoDatabaseFactory> mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory.getIfAvailable());
    }

    @Primary
    @Bean(name = "secondMongoDatabaseFactory")
    public MongoDatabaseFactory secondMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(multipleMongoProperties.getSecond().getUri());
    }
}
