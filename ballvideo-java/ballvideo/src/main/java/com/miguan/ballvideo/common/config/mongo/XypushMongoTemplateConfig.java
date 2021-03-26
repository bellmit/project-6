package com.miguan.ballvideo.common.config.mongo;

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
@EnableMongoRepositories(mongoTemplateRef = "xypushMongoTemplate")
public class XypushMongoTemplateConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.xypush")
    public MongoProperties xypushMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "xypushMongoTemplate")
    public MongoTemplate xypushMongoTemplate() throws Exception {
        return new MongoTemplate(xypushMongoFactory(xypushMongoProperties()));
    }

    @Bean
    public MongoDbFactory xypushMongoFactory(MongoProperties xypushMongoProperties) throws Exception     {
        return new SimpleMongoDbFactory(new MongoClientURI(xypushMongoProperties().getUri()));
    }
}
