package com.miguan.laidian.common.config.mongo;

import com.mongodb.MongoClientURI;
import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Data
@Configuration
@EnableMongoRepositories(mongoTemplateRef = "idmappingMongoTemplate")
public class IdmappingMongoTemplateConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.idmapping")
    public MongoProperties idmappingMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "idmappingMongoTemplate")
    public MongoTemplate idmappingMongoTemplate() throws Exception {
        return new MongoTemplate(idmappingMongoFactory(idmappingMongoProperties()));
    }

    @Bean
    public MongoDbFactory idmappingMongoFactory(MongoProperties idmappingMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(idmappingMongoProperties().getUri()));
    }
}
