CREATE TABLE  ad_advert_position  (
                                      `id` bigint(20) NOT NULL COMMENT '主要的*主键',
                                      `app_package` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '500*包名',
                                      `first_load_position` int(11) NULL DEFAULT NULL COMMENT '首次加载*首次加载',
                                      `max_show_num` int(11) NULL DEFAULT NULL COMMENT '展示次数限制*展示次数限制',
                                      `mobile_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机类型1*应用端:1-ios，2-安卓',
                                      `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '广告位名称',
                                      `position_type` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字*',
                                      `second_load_position` int(11) NULL DEFAULT NULL COMMENT '再加载*再次加载',
                                      `created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建时间*创建时间',
                                      `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
                                      `total_name` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '广告位总称',
                                      `report_show_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报表展示名称',
                                      `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态，0：关闭，1：启用',
                                      `last_operate_admin` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '最后操作人',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `pTypeUnique`(`app_package`, `position_type`) USING BTREE
) ENGINE=InnoDB CHARACTER SET = utf8mb4 COMMENT = '广告位置表（广告相关表统一ad_开头）' ROW_FORMAT = Compact;

CREATE TABLE video_sta  (
                            `id` bigint(20) NOT NULL COMMENT '主键',
                            `cat_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '分类id',
                            `num` int(11) NOT NULL DEFAULT 0 COMMENT '进文量/下线量',
                            `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0=下线 1=进文',
                            `date` date NOT NULL DEFAULT '0000-00-00' COMMENT '日期',
                            PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '统计视频进文量和下线量';

CREATE TABLE app_ad  (
                                          `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
                                          `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'app名称',
                                          `mobile_type` tinyint(4) UNSIGNED NULL DEFAULT 0 COMMENT '手机类型：1-ios 2-安卓',
                                          `sort` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '排序',
                                          `app_package` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '所属应用',
                                          `created_at` timestamp(0) NULL DEFAULT NULL,
                                          `updated_at` timestamp(0) NULL DEFAULT NULL,
                                          `app_id` int(11) NULL DEFAULT NULL COMMENT 'app表的id',
                                          `client_id` int(11) NULL DEFAULT NULL COMMENT '1=安卓,2=ios,3=小程序',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '应用 从广告库同步' ROW_FORMAT = Compact;

INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (1, '茜柚视频_安卓', 2, 0, 'com.mg.xyvideo', '2020-04-28 19:45:02', '2020-04-28 19:45:02', 2, 1);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (2, '果果视频_ios', 1, 0, 'com.mg.westVideo', '2020-04-28 19:45:02', '2020-04-28 19:45:02', 1, 2);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (3, '果果视频_安卓', 2, 0, 'com.mg.ggvideo', '2020-04-28 19:45:02', '2020-04-28 19:45:02', 1, 1);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (5, '蜜桃视频_安卓', 2, 0, 'com.mg.mtvideo', '2020-04-28 19:45:02', '2020-04-28 19:45:02', 3, 1);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (6, '炫来电', 2, 0, 'xld', '2020-05-07 16:20:02', '2020-05-07 16:20:02', 4, 1);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (7, '茜柚视频_ios', 1, 0, 'com.xm98.grapefruit', '2020-04-28 19:45:02', '2020-04-28 19:45:02', 2, 2);
INSERT INTO `app_ad`(`id`, `name`, `mobile_type`, `sort`, `app_package`, `created_at`, `updated_at`, `app_id`, `client_id`) VALUES (8, '茜柚视频小程序', 3, 0, 'com.mg.xyxcx', '2020-07-14 10:29:37', '2020-07-14 10:29:41', 2, 3);

CREATE TABLE rp_user_keep  (
                               `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                               `dd` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '计算日期，如20200801',
                               `package_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '应用包名，不使用该维度使用null',
                               `app_version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '应用版本，不使用该维度使用null',
                               `change_channel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新渠道，不使用该维度使用null',
                               `is_new` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '是否新设备 1-是新增，0-否，不使用该维度为null',
                               `type` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '类型 1-新增用户存留，2-活跃用户，不使用该维度为null',
                               `sd` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '选择日期，如20200801',
                               `user` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户数',
                               `keep_1` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '次日留存',
                               `keep_2` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '2日留存',
                               `keep_3` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '3日留存',
                               `keep_4` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '4日留存',
                               `keep_5` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '5日留存',
                               `keep_6` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '6日留存',
                               `keep_7` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '7日留存',
                               `keep_14` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '14日留存',
                               `keep_30` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '30日留存',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户留存数据表' ROW_FORMAT = Compact;