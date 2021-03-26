# 视频专辑表
CREATE TABLE `video_album` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(20) NOT NULL DEFAULT '' COMMENT '标题',
  `intro` varchar(255) NOT NULL DEFAULT '' COMMENT '简介',
  `cover_image_url`  varchar(255) NOT NULL DEFAULT ''  COMMENT '封面图片地址',
 `sort` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
 `status` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '状态:1=正常,-1=禁用',
  `created_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '新增时间',
  `updated_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='视频专辑表';


# 视频审核表
CREATE TABLE `video_examine` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
 `app_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '应用id',
 `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
 `video_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '视频id',
 `message_dictionary` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息字典id',
 `examine_status` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '审核状态:1=通过,-1=不通过',
 `created_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '新增时间',
  `updated_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='视频审核表';



# 消息字典表
CREATE TABLE `message_dictionary` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
 `project_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '项目id',
 `column_name`  varchar(100) NOT NULL DEFAULT '' COMMENT '栏目',
 `english_field`  varchar(100) NOT NULL DEFAULT '' COMMENT '英文字段',
 `chinese_explain`  varchar(100) NOT NULL DEFAULT '' COMMENT '中文说明',
 `created_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '新增时间',
  `updated_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='消息字典表';

#菜单栏表新增tips字段
ALTER TABLE `cl_menu_config` ADD COLUMN `tips` varchar(50) NULL COMMENT 'tips提示语';



CREATE TABLE `video_album_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `video_album_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '专辑id',
  `first_videos_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '短视频id',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `sort` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
  `created_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '新增时间',
  `updated_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='视频专辑详情表（关键）';


#视频专辑详情表新增索引
ALTER TABLE `video_album_detail` ADD INDEX `index_video_album_id`(`video_album_id`) USING BTREE COMMENT '专辑ID索引';

#系统消息新增字段

ALTER TABLE `cl_user_opinion` ADD COLUMN `title` varchar(255) NULL COMMENT '标题';
ALTER TABLE `cl_user_opinion` ADD COLUMN `type` tinyint(3) NULL COMMENT '1.app启动 2.链接 3.短视频 4.小视频 5.系统消息';
ALTER TABLE `cl_user_opinion` ADD COLUMN `link` varchar(255) NULL COMMENT '链接';
ALTER TABLE `cl_user_opinion` ADD COLUMN `type_value` varchar(255) NULL COMMENT 'type_value保存type对应的值（URL，视频ID等)';

ALTER TABLE `push_article` ADD COLUMN `user_ids` longtext NULL COMMENT '用户id，多个用逗号隔开';

INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '观看的短视频Id每几条更新到数据库', 'video_watch_limit', '10', '1', '观看的短视频Id每几条更新到数据库', '1', now(),  now());
INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '激励视频用户可预览视频时长', 'incentive_video_rate', '20', '1', '配置内容：0%~100%；字段用于控制激励视频播放多久后出现激励广告需求', '1', NOW(), NOW());
INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '激励视频排序位置', 'incentive_video_position', '1', '1', '配置内容：1~8；字段用于控制将激励视频排序在第N个位置', '1', NOW(), NOW());
INSERT INTO arc_sys_config ( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('10', '激励视频开关', 'incentive_video_switch', '0', '1', '激励视频开关：1-开  0-关', '1', NOW(), NOW());
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('10', '曝光视频开关', 'video_exposure_switch', '1', '1', '曝光视频开关 1-开  0-关', '1', NOW(), NOW());

# 埋点表新增字段
alter table xy_burying_point add unlock_type int(3) DEFAULT NULL COMMENT '用户解锁方式，10不解锁；11首页点击；12提前解锁；13弹窗解锁';
alter table xy_burying_point add album_id int(11) DEFAULT NULL COMMENT '专辑id';
alter table xy_burying_point add album_name varchar(50) DEFAULT NULL COMMENT '专辑名称';

# 设备token 新增表
CREATE TABLE `cl_device`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备ID',
  `state` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '10' COMMENT '状态（10 正常 20 禁用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `device_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '友盟对设备的唯一标识',
  `huawei_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '华为对设备的唯一标识',
  `vivo_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vivo对设备的唯一标识',
  `oppo_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'oppo对设备的唯一标识',
  `xiaomi_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小米对设备的唯一标识',
  `app_package` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '包名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `device_id_app_package`(`device_id`, `app_package`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;


# 初始数据需要从 cl_user 捞,需要对旧的数据做一个处理:需要运维评估数据量先
INSERT INTO cl_device ( device_id, device_token, huawei_token, oppo_token, xiaomi_token, vivo_token, app_package ) SELECT DISTINCT
	device_id,
	device_token,
	huawei_token,
	oppo_token,
	xiaomi_token,
	vivo_token,
	'com.mg.xyvideo'
FROM
	cl_user
WHERE
	device_id <> ''
	AND device_id IS NOT NULL

alter table ad_error_count_log add app_time bigint(11) DEFAULT NULL COMMENT '更新时间';

ALTER TABLE first_videos
	ADD COLUMN click_count bigint(11) NULL DEFAULT 1 COMMENT '点击数' AFTER watch_count;
ALTER TABLE first_videos
	ADD COLUMN show_count bigint(11) NULL DEFAULT 1 COMMENT '曝光数' AFTER click_count;
ALTER TABLE first_videos
	ADD COLUMN `ctr` decimal(5, 4) NULL DEFAULT 0.0000 COMMENT 'crt值' AFTER show_count;

ALTER TABLE first_videos
	ADD COLUMN exposure_num int(11) NULL DEFAULT 0 COMMENT '视频最大可曝光次数';

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'Ctr从redis写入到es间隔(分钟)', 'update_ctr_redis_es', '10', 1, 'Ctr从redis写入到es间隔(分钟)', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'Ctr从redis写入到db间隔(分钟)', 'update_ctr_redis_db', '10', 1, 'Ctr从redis写入到db间隔(分钟)', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'redis缓存设备点击视频记录(秒)', 'redis_deviceId_videoId_expired', '302400', 1, 'redis缓存设备点击视频记录(秒)', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'redis的show每隔多少条自增加', 'redis_inc_ctr_show_limit', '100', 1, 'redis的show每隔多少条自增加', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'redis的click每隔多少条自增加', 'redis_inc_ctr_click_limit', '10', 1, 'redis的click每隔多少条自增加', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('108', '20', '用户ES浏览记录（曝光记录）缓存有效期', 'user_es_showedIds_expiredTime', '302400', '1', 'ES存放时间', '1', NULL, '2020-04-21 21:47:34');

CREATE TABLE `video_exposure_total_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `video_id` int(11) DEFAULT '0' COMMENT '视频ID',
  `total_count` int(11) unsigned DEFAULT '0' COMMENT '曝光总次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='曝光视频统计总数表';


CREATE TABLE `video_exposure_one_hour_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `video_id` int(11) DEFAULT '0' COMMENT '视频ID',
  `count` int(11) DEFAULT '0' COMMENT '曝光次数',
  `create_time` int(11) DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='曝光视频统计1小时表';

INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '总曝光视频每几条统计到数据库', 'video_exposure_total_limit', '10', '1', '总曝光视频每几条统计到数据库', '1', NOW(), NOW());
INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '1小时曝光视频每几条统计到数据库', 'video_exposure_one_hour_limit', '10', '1', '1小时曝光视频每几条统计到数据库', '1', NOW(), NOW());
INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '查询的曝光视频每几条统计进行处理', 'video_exposure_limit', '10', '1', '查询的曝光视频每几条统计进行处理', '1', NOW(), NOW());
INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '用户历史曝光视频总数redis存储天数', 'video_exposure_history_day', '7', '1', '用户历史曝光视频总数redis存储天数', '1', NOW(), NOW());

CREATE TABLE `first_video_extend` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增id',
  `video_id` bigint(10) unsigned NOT NULL DEFAULT '0' COMMENT 'first_video 表的id',
  `hour_click_rate` varchar(20) NOT NULL DEFAULT '' COMMENT '1小时点击率',
  `total_click_rate` varchar(20) NOT NULL DEFAULT '' COMMENT '总点击率',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_video_id` (`video_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='first_video表的统计扩展';

CREATE TABLE `video_view_report` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `video_id` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '视频id',
  `incentive_play` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '激励视频播放数',
  `ad_views` int(10) NOT NULL DEFAULT '0' COMMENT '观看广告数',
  `views_rate` double(10,2) NOT NULL DEFAULT '0.00' COMMENT '观看率',
  `home_click` int(10) NOT NULL DEFAULT '0' COMMENT '首页点击',
  `advance_unlock` int(10) NOT NULL DEFAULT '0' COMMENT '提前解锁',
  `popup_unlock` int(10) NOT NULL DEFAULT '0' COMMENT '弹窗解锁',
  `date` date NOT NULL DEFAULT '0000-00-00' COMMENT '日期',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='激励视频 查看数据报表';

CREATE TABLE `video_album_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `app_package` varchar(50) DEFAULT 'com.mg.xyvideo' COMMENT 'app',
  `video_album_id` int(11) unsigned DEFAULT '0' COMMENT '视频专辑ID',
  `title` varchar(20) NOT NULL DEFAULT '' COMMENT '标题',
  `video_quantity` int(11) unsigned DEFAULT '0' COMMENT '视频数',
  `click_user_quantity` int(11) unsigned DEFAULT '0' COMMENT '专辑点击人数',
  `click_quantity` int(11) unsigned DEFAULT '0' COMMENT '专辑点击次数',
  `play_user_quantity` int(11) unsigned DEFAULT '0' COMMENT '专辑播放用户数',
  `play_quantity` int(11) unsigned DEFAULT '0' COMMENT '专辑播放次数',
  `effective_play_user_quantity` int(11) unsigned DEFAULT '0' COMMENT '视频有效播放用户数',
  `play_conversion_rate` decimal(10,2) unsigned DEFAULT '0.00' COMMENT '进入专辑播放转化率',
  `average_user_play_quantity` int(11) unsigned DEFAULT '0' COMMENT '进入专辑人均播放次数',
  `average_user_play_hour_quantity` int(11) unsigned DEFAULT '0' COMMENT '人均播放时长',
  `effective_play_quantity` int(11) unsigned DEFAULT '0' COMMENT '有效播放数',
  `effective_play_rate` decimal(20,2) unsigned DEFAULT '0.00' COMMENT '有效播放率',
  `effective_conversion_rate` decimal(20,2) unsigned DEFAULT '0.00' COMMENT '有效转化率',
  `average_effective_user_play_quantity` int(11) unsigned DEFAULT '0' COMMENT '人均有效播放数',
  `full_play_quantity` int(11) unsigned DEFAULT '0' COMMENT '完播数',
  `full_play_quantity_rate` decimal(10,2) unsigned DEFAULT '0.00' COMMENT '完播率',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='视频专辑报表';

ALTER TABLE first_videos ADD compare_show_expo TINYINT(4) DEFAULT '1' COMMENT '比较show和最大曝光结构,优化es查询,show>expo 取-1,否则取1,初始值1';

INSERT INTO `arc_sys_config`(`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES (20, '用户曝光的视频id的上限', 'exposure_max_limit', '3500', 1, 'exposure_max_limit', 1, '2020-06-12 19:34:58', NULL);

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'redis 记录 show 队列堆积上限', 'redis_queue_show_limit', '50000', 1, 'redis 记录 show 队列堆积上限', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'redis 记录 click 队列堆积上限', 'redis_queue_click_limit', '50000', 1, 'redis 记录 show 队列堆积上限', 1, '2020-06-08 11:37:55', '2020-06-08 11:46:38');

#已执行============================================20200628
CREATE TABLE `video_click_one_hour_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `video_id` int(11) DEFAULT 0 COMMENT '视频ID',
  `count` int(11) DEFAULT 0 COMMENT '点击次数',
  `create_time` int(11) DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `video_id` (`video_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='点击视频统计1小时表';


CREATE TABLE `videos_share` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `video_id` bigint(11) NOT NULL COMMENT '视频Id',
  `share_type` int(2) DEFAULT 1 COMMENT '分享类型 0：小程序分享 1：H5分享',
  `share_count` bigint(11) DEFAULT 0 COMMENT '视频分享数',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unikey` (`video_id`,`share_type`) USING BTREE,
  KEY `video_id` (`video_id`,`share_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='视频分享记录表';


INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '小程序分享地址', 'shareWeChat', 'pages/videoDetails/index', '1', '小程序分享地址', '1', NOW(), NOW());
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '小程序userName', 'share_user_name', 'gh_da7f5d13faaa', '1', '小程序userName', '1', NOW(), NOW());
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('10', '分享功能开关', 'share_switch', '0', '1', '0:小程序分享、1:H5链接分享', '1', NOW(), NOW());
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('10', '分享次数配置', 'share_number', '0,10;1,10', '1', '0,N;1,M:0：小程序分享;1：H5链接分享;N：小程序限制单个视频分享N次;M：H5链接限制单个视频分享M次;-1：代表不限制分享次数', '1', NOW(), NOW());
#已执行============================================20200628

INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('10', '1小时点击视频每几条统计到数据库', 'video_click_one_hour_limit', '10', '1', '1小时点击视频每几条统计到数据库', '1', NOW(), NOW());

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, '记录的设备曝光的视频过期时间(天)', 'redis_device_show_exp_day', '1', 1, '记录的设备曝光的视频过期时间(天)', 1, NOW(), NOW());

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, '记录的设备点击的视频过期时间(天)', 'redis_device_click_exp_day', '1', 1, '记录的设备点击的视频过期时间(天)', 1, NOW(), NOW());

#已执行============================================20200630

INSERT INTO arc_sys_config (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`) VALUES ('20', '专辑首页插屏广告隔多少天触发', 'album_video_screen_day', '2', '1', '专辑首页插屏广告隔多少天触发', '1', NOW(), NOW();

 CREATE TABLE `warn_keyword_ios`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '敏感词',
  `created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14600 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词管理' ROW_FORMAT = Compact;

INSERT INTO `warn_keyword_ios` (keyword,created_at,updated_at) VALUES ('美帝', '2020-07-03 14:52:54', '2020-07-03 14:52:54');

INSERT INTO `arc_sys_config`( `type`, `name`, `code`, `value`, `status`, `remark`, `creator`, `created_at`, `updated_at`)
 VALUES (20, 'ios是否开启搜索敏感词过滤', 'ios_sensitive_switch', '1', 1, 'ios是否开启搜索敏感词过滤', 1, '2020-07-03 11:37:55', '2020-07-03 11:46:38');


