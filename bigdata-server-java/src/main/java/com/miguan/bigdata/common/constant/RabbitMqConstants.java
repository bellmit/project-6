package com.miguan.bigdata.common.constant;

public class RabbitMqConstants {

    public final static String NUPUSH_TYPE_INTEREST_CAT = "interestCat";
    public final static String NUPUSH_TYPE_ = "interestCat";

    // 生产者：NPUSH； 消费者：大数据
    public final static String NPUSH_POINT_BIGDATA_POOL_EXCHANGE = "npush.producer.pool.exchange";
    public final static String NPUSH_POINT_BIGDATA_POOL_QUEUE = "npush.producer.pool.queue";
    public final static String NPUSH_POINT_BIGDATA_POOL_RUTEKEY = "npush.producer.pool.key";

    // 生产者：大数据； 消费者：NPUSH
    public final static String BIGDATA_POINT_NPUSH_POOL_EXCHANGE = "npush.consumer.pool.exchange";
    public final static String BIGDATA_POINT_NPUSH_POOL_RUTEKEY = "npush.consumer.pool.key";
    public final static String BIGDATA_POINT_NPUSH_POOL_QUEUE = "npush.consumer.pool.queue";

    // 生产者：大数据； 消费者：大数据
    public final static String BIGDATA_POINT_NPUSH_INIT_EXCHANGE = "npush.distinct.init.exchange";
    public final static String BIGDATA_POINT_NPUSH_INIT_RUTEKEY = "npush.distinct.init.key";
    public final static String BIGDATA_POINT_NPUSH_INIT_QUEUE = "npush.distinct.init.queue";
}
