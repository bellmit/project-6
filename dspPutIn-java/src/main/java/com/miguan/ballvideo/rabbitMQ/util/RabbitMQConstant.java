package com.miguan.ballvideo.rabbitMQ.util;

public interface RabbitMQConstant {

  String _MQ_ = "@";

  // 用户标签埋点
  String BURYPOINT_LABEL_QUEUE = "xy.send.burypoint.queue";
  String BURYPOINT_LABEL_EXCHANGE = "xy.send.burypoint.exchange";
  String BURYPOINT_LABEL_KEY = "xy.send.burypoint.rutekey";

  // 用户操作埋点
  String BURYPOINT_QUEUE = "xy.new.send.newburypoint.queue";
  String BURYPOINT_EXCHANGE = "xy.new.send.newburypoint.exchange";
  String BURYPOINT_RUTE_KEY = "xy.new.send.newburypoint.rutekey";

  // 用户标签初始化缓存
  String UserLabel_QUEUE = "xy.userLabel.queue";
  String UserLabel_EXCHANGE = "xy.userLabel.exchange";
  String UserLabel_KEY = "xy.userLabel.key";

  // 用户标签保存
  String UserLabel_SAVE_QUEUE = "xy.userLabel.save.queue";
  String UserLabel_SAVE_EXCHANGE = "xy.userLabel.save.exchange";
  String UserLabel_SAVE_KEY = "xy.userLabel.save.key";

  // 系统消息保存
  String UserOption_SAVE_QUEUE = "xy.userOption.save.queue";
  String UserOption_SAVE_EXCHANGE = "xy.userOption.save.exchange";
  String UserOption_SAVE_KEY = "xy.userOption.save.key";

  // 曝光记录到推荐mq处理
  String RCMD_REDIS_DEVICE_SHOW_SAVE_QUEUE = "xy.rcmd.redisDeviceShow.save.queue";
  String RCMD_REDIS_DEVICE_SHOW_SAVE_EXCHANGE = "xy.rcmd.redisDeviceShow.save.exchange";
  String RCMD_REDIS_DEVICE_SHOW_SAVE_KEY = "xy.rcmd.redisDeviceShow.save.key";

  // Redis 的 Ctr 保存到 show

  String RCMD_CTR_REDIS_SHOW_SAVE_QUEUE = "xy.rcmd.ctrRedisShow.save.queue";
  String RCMD_CTR_REDIS_SHOW_SAVE_EXCHANGE = "xy.rcmd.ctrRedisShow.save.exchange";
  String RCMD_CTR_REDIS_SHOW_SAVE_KEY = "xy.rcmd.ctrRedisShow.save.key";

  // Redis 的 Ctr 保存到 Click

  String RCMD_CTR_REDIS_CLICK_SAVE_QUEUE = "xy.rcmd.ctrRedisClick.save.queue";
  String RCMD_CTR_REDIS_CLICK_SAVE_EXCHANGE = "xy.rcmd.ctrRedisClick.save.exchange";
  String RCMD_CTR_REDIS_CLICK_SAVE_KEY = "xy.rcmd.ctrRedisClick.save.key";

  // Redis 的 Ctr 保存到 Es

  String RCMD_CTR_REDIS_ES_SAVE_QUEUE = "xy.rcmd.ctrRedisEs.save.queue";
  String RCMD_CTR_REDIS_ES_SAVE_EXCHANGE = "xy.rcmd.ctrRedisEs.save.exchange";
  String RCMD_CTR_REDIS_ES_SAVE_KEY = "xy.rcmd.ctrRedisEs.save.key";

  // Redis 的 Ctr 保存到 Db

  String RCMD_CTR_REDIS_DB_SAVE_QUEUE = "xy.rcmd.ctrRedisDb.save.queue";
  String RCMD_CTR_REDIS_DB_SAVE_EXCHANGE = "xy.rcmd.ctrRedisDb.save.exchange";
  String RCMD_CTR_REDIS_DB_SAVE_KEY = "xy.rcmd.ctrRedisDb.save.key";

    // 推送埋点
  String BURYPOINT_PUSH_QUEUE = "xy.send.push.burypoint.queue";
  String BURYPOINT_PUSH_EXCHANGE = "xy.send.push.burypoint.exchange";
  String BURYPOINT_PUSH_KEY = "xy.send.push.burypoint.rutekey";

  // 视频评论初始化
  String VIDEOS_COMMENT_QUEUE = "xy.send.videos.comment.queue";
  String VIDEOS_COMMENT_EXCHANGE = "xy.send.videos.comment.exchange";
  String VIDEOS_COMMENT_KEY = "xy.send.videos.comment.rutekey";

  //日志记录埋点
  String OPERATE_LOG_QUEUE = "xy.operateLog.queue";
  String OPERATE_LOG_EXCHANGE = "xy.operateLog.exchange";
  String OPERATE_LOG_KEY = "xy.operateLog.key";

  // 用户标签权重分更新
  String USERLABELGRADE_QUEUE = "xy.userLabelGrade.queue";
  String USERLABELGRADE_EXCHANGE = "xy.userLabelGrade.exchange";
  String USERLABELGRADE_KEY = "xy.userLabelGrade.key";

  //视频生成索引数据
  String VIDEOS_ES_SEARCH_QUEUE = "xy.video.es.queue";
  String VIDEOS_ES_SEARCH_EXCHANGE = "xy.video.es.exchange";
  String VIDEOS_ES_SEARCH_KEY = "xy.video.es.key";

  //修改观看数，点赞数等相关视频操作
  String VIDEO_UPDATECOUNT_QUEUE = "xy.video.updateCount.queue";
  String VIDEO_UPDATECOUNT_EXCHANGE = "xy.video.updateCount.exchange";
  String VIDEO_UPDATECOUNT_KEY = "xy.video.updateCount.key";

  //修改观看数相关视频操作
  String VIDEO_WATCH_UPDATECOUNT_QUEUE = "xy.video.watch.updateCount.queue";
  String VIDEO_WATCH_UPDATECOUNT_EXCHANGE = "xy.video.watch.updateCount.exchange";
  String VIDEO_WATCH_UPDATECOUNT_KEY = "xy.video.watch.updateCount.key";

  //修改视频真实权重
  String VIDEO_REALWEIGHT_UPDATE_QUEUE = "xy.video.realweight.update.queue";
  String VIDEO_REALWEIGHT_UPDATE_EXCHANGE = "xy.video.realweight.update.exchange";
  String VIDEO_REALWEIGHT_UPDATE_KEY = "xy.video.realweight.update.key";

  //用户标签预期未操作删除操作
  String UserLabel_DELETE_QUEUE = "xy.userLabel.delete.queue";
  String UserLabel_DELETE_EXCHANGE = "xy.userLabel.delete.exchange";
  String UserLabel_DELETE_KEY = "xy.userLabel.delete.key";

  //广告展示错误埋点保存
  String AD_ERROR_QUEUE = "xy.ad.error.queue";
  String AD_ERROR_EXCHANGE = "xy.ad.error.exchange";
  String AD_ERROR_KEY = "xy.ad.error.key";

  //广告展示错误统计埋点保存
  String AD_ERROR_COUNT_QUEUE = "xy.ad.error.count.queue";
  String AD_ERROR_COUNT_EXCHANGE = "xy.ad.error.count.exchange";
  String AD_ERROR_COUNT_KEY = "xy.ad.error.count.key";

  //观看视频Redis保存
  String VIDEO_ADD_REDISE_WATCH_QUEU = "xy.video.add.redis.watch.queue";
  String VIDEO_ADD_REDIS_WATCH_EXCHANGE = "xy.video.add.redis.watch.exchange";
  String VIDEO_ADD_REDIS_WATCH_KEY = "xy.video.add.redis.watch.key";

  //视频曝光统计保存
  String VIDEO_EXPOSURE_COUNT_QUEUE = "xy.video.exposure.count.queue";
  String VIDEO_EXPOSURE_COUNT_EXCHANGE = "xy.video.exposure.count.exchange";
  String VIDEO_EXPOSURE_COUNT_KEY = "xy.video.exposure.count.key";

  // 视频1小時观看数保存到Redis
  String VIDEO_HOUR_CLICK_SAVE_QUEUE = "xy.video.hour.click.save.queue";
  String VIDEO_HOUR_CLICK_SAVE_EXCHANGE = "xy.video.hour.click.save.exchange";
  String VIDEO_HOUR_CLICK_SAVE_KEY = "xy.video.hour.click.save.key";
}
