package com.miguan.recommend.common.constants;

public class RabbitMqConstants {

    public final static String _MQ_ = "@";
    //初始mongodb视频权重表
    public final static String HOTSPOST_INIT_QUEUE = "hotspost.init.queue";
    public final static String HOTSPOST_INIT_EXCHANGE = "hotspost.init.exchange";
    public final static String HOTSPOST_INIT_RUTE_KEY = "hotspost.init.rutekey";

    //视频推荐相关操作(上线，下线)
    public final static String VIDEO_REC_QUEUE = "xy.video.rec.queue";
    public final static String VIDEO_REC_EXCHANGE = "xy.video.rec.exchange";
    public final static String VIDEO_REC_KEY = "xy.video.rec.key";

    // 生产者：NPUSH； 消费者：大数据
    public final static String NPUSH_POINT_BIGDATA_POOL_EXCHANGE = "npush.producer.pool.exchange";
    public final static String NPUSH_POINT_BIGDATA_POOL_QUEUE = "npush.producer.pool.queue";
    public final static String NPUSH_POINT_BIGDATA_POOL_RUTEKEY = "npush.producer.pool.key";

    // 生产者：大数据； 消费者：NPUSH
    public final static String BIGDATA_POINT_NPUSH_POOL_EXCHANGE = "npush.consumer.pool.exchange";
    public final static String BIGDATA_POINT_NPUSH_POOL_QUEUE = "npush.consumer.pool.queue";
    public final static String BIGDATA_POINT_NPUSH_POOL_RUTEKEY = "npush.consumer.pool.key";
}
