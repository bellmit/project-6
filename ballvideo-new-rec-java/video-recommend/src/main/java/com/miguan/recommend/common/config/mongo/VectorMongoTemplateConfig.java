//package com.miguan.recommend.common.config.mongo;
//
//import com.mongodb.MongoClientURI;
//import lombok.Data;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//@Data
//@Configuration
//@EnableMongoRepositories(mongoTemplateRef = "vectorMongoTemplate")
//public class VectorMongoTemplateConfig {
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.data.mongodb.vector")
//    public MongoProperties vectorMongoProperties() {
//        return new MongoProperties();
//    }
//
//    @Bean(name = "vectorMongoTemplate")
//    public MongoTemplate featureMongoTemplate() throws Exception {
//        return new MongoTemplate(vectorMongoFactory(vectorMongoProperties()));
//    }
//
//    @Bean
//    public MongoDbFactory vectorMongoFactory(MongoProperties featureMongoProperties) throws Exception     {
//        return new SimpleMongoDbFactory(new MongoClientURI(vectorMongoProperties().getUri()));
//    }
//}