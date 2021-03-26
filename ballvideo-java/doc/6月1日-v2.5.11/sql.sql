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