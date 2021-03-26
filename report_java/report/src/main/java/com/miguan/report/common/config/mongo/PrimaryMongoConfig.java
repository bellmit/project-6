package com.miguan.report.common.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author zhongli
 * @date 2020-06-28 
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.miguan.report.repository.mongo.primary",
        mongoTemplateRef = PrimaryMongoConfig.MONGO_TEMPLATE)
public class PrimaryMongoConfig {
    public static final String MONGO_TEMPLATE = "primaryMongoTeamplate";
}
