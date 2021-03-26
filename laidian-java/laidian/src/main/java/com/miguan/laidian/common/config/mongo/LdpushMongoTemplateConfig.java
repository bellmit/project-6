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
@EnableMongoRepositories(mongoTemplateRef = "ldpushMongoTemplate")
public class LdpushMongoTemplateConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.ldpush")
    public MongoProperties ldpushMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "ldpushMongoTemplate")
    public MongoTemplate ldpushMongoTemplate() throws Exception {
        return new MongoTemplate(ldpushMongoFactory(ldpushMongoProperties()));
    }

    @Bean
    public MongoDbFactory ldpushMongoFactory(MongoProperties ldpushMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(ldpushMongoProperties().getUri()));
    }
}
