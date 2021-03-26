package com.miguan.bigdata.common.config.mongo;

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
@EnableMongoRepositories(mongoTemplateRef = "driveMongoTemplate")
public class DriveMongoTemplateConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.drive")
    public MongoProperties driveMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "driveMongoTemplate")
    public MongoTemplate driveMongoTemplate() throws Exception {
        return new MongoTemplate(driveMongoFactory(driveMongoProperties()));
    }

    @Bean
    @Primary
    public MongoDbFactory driveMongoFactory(MongoProperties driveMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(driveMongoProperties().getUri()));
    }
}
