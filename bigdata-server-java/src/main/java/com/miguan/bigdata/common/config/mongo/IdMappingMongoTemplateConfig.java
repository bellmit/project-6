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
@EnableMongoRepositories(mongoTemplateRef = "idMappingMongoTemplate")
public class IdMappingMongoTemplateConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.idmapping")
    public MongoProperties idMappingMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "idMappingMongoTemplate")
    public MongoTemplate idMappingMongoTemplate() throws Exception {
        return new MongoTemplate(idMappingMongoFactory(idMappingMongoProperties()));
    }

    @Bean
    @Primary
    public MongoDbFactory idMappingMongoFactory(MongoProperties idMappingMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(idMappingMongoProperties().getUri()));
    }
}
