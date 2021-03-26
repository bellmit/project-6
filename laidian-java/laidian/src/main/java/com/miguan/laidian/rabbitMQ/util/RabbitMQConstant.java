package com.miguan.laidian.rabbitMQ.util;

public interface RabbitMQConstant {

    //埋点
    String BURYPOINT_QUEUE = "ld.send.burypoint.queue";
    String BURYPOINT_EXCHANGE = "ld.send.burypoint.exchange";
    String BURYPOINT_RUTE_KEY = "ld.send.burypoint.rutekey";

    //活动埋点
    String BURYPOINT_ACTIVITY_QUEUE = "ld.send.burypoint.activity.queue";
    String BURYPOINT_ACTIVITY_EXCHANGE = "ld.send.burypoint.activity.exchange";
    String BURYPOINT_ACTIVITY_RUTE_KEY = "ld.send.burypoint.activity.rutekey";

    //通讯录
    String USERCONTACT_QUEUE = "ld.send.burypoint.queue.TOPIC_userContact_MQ";
    String USERCONTACT_EXCHANGE = "ld.send.burypoint.exchange.TOPIC_userContact_MQ";
    String USERCONTACT_RUTE_KEY = "ld.send.burypoint.rutekey.TOPIC_userContact_MQ";

    String TOPIC_USERBURIEDPOINT_MQ = "Topic_UserBuriedPoint_MQ";
    String TOPIC_USER_BURIED_POINT_ADDITIONAL_MQ = "Topic_UserBuriedPointAdditional_MQ";
    String TOPIC_LDBURYINGPOINTEVERY_MQ = "Topic_LdBuryingPointEvery_MQ";

    //更新用户收藏数量、分享数量、点击数
    String VIDEO_UPDATECOUNT_QUEUE = "ld.video.updateCount.queue";
    String VIDEO_UPDATECOUNT_EXCHANGE = "ld.video.updateCount.exchange";
    String VIDEO_UPDATECOUNT_KEY = "ld.video.updateCount.key";

    //广告展示错误埋点保存
    String AD_ERROR_QUEUE = "ld.ad.error.queue";
    String AD_ERROR_EXCHANGE = "ld.ad.error.exchange";
    String AD_ERROR_KEY = "ld.ad.error.key";

    //广告展示错误统计埋点保存
    String AD_ERROR_COUNT_QUEUE = "ld.ad.error.count.queue";
    String AD_ERROR_COUNT_EXCHANGE = "ld.ad.error.count.exchange";
    String AD_ERROR_COUNT_KEY = "ld.ad.error.count.key";

    //V2.6.0音频,更新用户收藏数、分享数、下载数、试听数
    String AUDIO_UPDATECOUNT_QUEUE = "ld.audio.updateCount.queue";
    String AUDIO_UPDATECOUNT_EXCHANGE = "ld.audio.updateCount.exchange";
    String AUDIO_UPDATECOUNT_KEY = "ld.audio.updateCount.key";

    //统计视频曝光数（不去重）
    String VIDEO_EXPOSURE_QUEUE = "ld.video.exposure.queue";
    String VIDEO_EXPOSURE_EXCHANGE = "ld.video.exposure.exchange";
    String VIDEO_EXPOSURE_KEY = "ld.video.exposure.key";

    //感兴趣视频ID列表
    String VIDEO_INTEREST_ID_QUEUE = "ld.video.interest.id.queue1";
    String VIDEO_INTEREST_ID_EXCHANGE = "ld.video.interest.id.exchange1";
    String VIDEO_INTEREST_ID_KEY = "ld.video.interest.id.key1";

    //vivo无效token保存到redis
    String PUSH_VIVO_ERROR_QUEUE = "ld.push.vivo.error.queue";
    String PUSH_VIVO_ERROR_EXCHANGE = "ld.push.vivo.error.exchange";
    String PUSH_VIVO_ERROR_KEY = "ld.push.vivo.error.key";

    //钉钉发送意见反馈消息
    String DINGTALK_ROBOT_MSG_QUEUE = "ld.dingtalk.robot.msg.queue";
    String DINGTALK_ROBOT_MSG_EXCHANGE = "ld.dingtalk.robot.msg.exchange";
    String DINGTALK_ROBOT_MSG_KEY = "ld.dingtalk.robot.msg.key";
}
