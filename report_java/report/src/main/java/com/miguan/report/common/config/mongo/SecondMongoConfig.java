package com.miguan.report.common.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author zhongli
 * @date 2020-06-28 
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.miguan.report.repository.mongo.second",
        mongoTemplateRef = SecondMongoConfig.MONGO_TEMPLATE)
public class SecondMongoConfig {
    public static final String MONGO_TEMPLATE = "secondMongoTeamplate";
}
