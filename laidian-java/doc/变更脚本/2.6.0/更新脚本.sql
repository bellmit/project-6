alter table ad_error add `app_time` bigint(11) DEFAULT NULL;
alter table ad_error add sdk varchar(50) DEFAULT NULL COMMENT 'sdk版本号';
alter table ad_error_count_log add sdk varchar(50) DEFAULT NULL COMMENT 'sdk版本号';

CREATE TABLE `ld_burying_point_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `point_type` varchar(32) DEFAULT NULL COMMENT '埋点类型',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `device_id` varchar(256) DEFAULT NULL COMMENT '设备id',
  `is_new` int(2) DEFAULT NULL COMMENT '新老用户:10-新用户，20老用户',
  `app_version` varchar(32) DEFAULT NULL COMMENT 'APP应用的版本',
  `os_version` varchar(32) DEFAULT NULL COMMENT '操作系统版本',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `channel_id` varchar(64) DEFAULT NULL COMMENT '来源渠道ID',
  `activity_id` bigint(20) DEFAULT NULL COMMENT '活动ID',
  `activity_name` varchar(128) DEFAULT NULL COMMENT '活动名称',
  `entrance_type` int(1) DEFAULT NULL COMMENT '入口类型 1.首页悬浮窗2.【我的】banner3.【首页】弹窗',
  `is_lottery` int(1) DEFAULT NULL COMMENT '是否抽奖 1.是 2.否',
  `prize_resource` int(1) DEFAULT NULL COMMENT '奖品来源 1.抽奖获得2.签到获得',
  `receive_result` int(1) DEFAULT NULL COMMENT '领取结果 1.立即领取2.观看视频抽手机3.关闭弹窗',
  `last_num` int(11) DEFAULT NULL COMMENT '剩余抽奖次数',
  `name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `account` varchar(64) DEFAULT NULL COMMENT '账号',
  `phone` varchar(64) DEFAULT NULL COMMENT '联系方式（手机号/QQ号）',
  `prize_name` varchar(64) DEFAULT NULL COMMENT '奖品名称',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '商品成本',
  `task_type` int(1) DEFAULT NULL COMMENT '此次完成任务类型 1.观看视频2.设置来电秀成功3.设置铃声成功',
  `package_name` varchar(16) DEFAULT NULL COMMENT '包名',
  PRIMARY KEY (`id`),
  KEY `device_id` (`device_id`(191)),
  KEY `app_version` (`app_version`),
  KEY `is_new` (`is_new`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='来电活动埋点表';