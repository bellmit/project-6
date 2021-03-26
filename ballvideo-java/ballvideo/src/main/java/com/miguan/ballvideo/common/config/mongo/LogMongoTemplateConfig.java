package com.miguan.ballvideo.common.config.mongo;

import com.mongodb.MongoClientURI;
import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Data
@Configuration
@EnableMongoRepositories(mongoTemplateRef = "logMongoTemplate")
public class LogMongoTemplateConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.log")
    public MongoProperties logMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "logMongoTemplate")
    public MongoTemplate logMongoTemplate() throws Exception {
        return new MongoTemplate(logMongoFactory(logMongoProperties()));
    }

    @Bean
    @Primary
    public MongoDbFactory logMongoFactory(MongoProperties logMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(logMongoProperties().getUri()));
    }
}
