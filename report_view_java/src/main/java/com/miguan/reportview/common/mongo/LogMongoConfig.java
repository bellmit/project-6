package com.miguan.reportview.common.mongo;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class LogMongoConfig {

    @Value("${spring.data.mongodb.log.uri}")
    public String uri;

    public MongoDbFactory mongoDbFactory() throws Exception {
        MongoClientURI mongoclienturi = new MongoClientURI(uri);
        return new SimpleMongoDbFactory(mongoclienturi);
    }

    @Primary
    @Bean(name = "logMongoTemplate")
    public MongoTemplate getMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
