-- 2020/7/1 创建索引
ALTER TABLE `banner_data_total_name`
    ADD INDEX `idx_date`(`date`) USING BTREE,
    ADD INDEX `idx_app_id`(`app_id`) USING BTREE,
    ADD INDEX `idx_ad_space`(`ad_space`) USING BTREE,
    ADD INDEX `idx_app_name`(`app_name`) USING BTREE;

ALTER TABLE `banner_data`
    ADD INDEX `idx_total_name`(`total_name`) USING BTREE,
    ADD INDEX `idx_cut_app_name`(`cut_app_name`) USING BTREE,
    ADD INDEX `idx_ad_apace_id`(`ad_space_id`) USING BTREE;

ALTER TABLE `umeng_data`
    ADD INDEX `idx_date`(`app_id`) USING BTREE;

ALTER TABLE `shence_data`
    ADD INDEX `idx_app_id`(`app_id`) USING BTREE;

ALTER TABLE `app_use_time`
    ADD INDEX `idx_use_day`(`use_day`) USING BTREE;

ALTER TABLE `hour_data`
    ADD INDEX `idx_date`(`date`) USING BTREE,
    ADD INDEX `idx_ad_id`(`ad_id`) USING BTREE;

ALTER TABLE `banner_quota_relation`
    ADD INDEX `idx_user_id`(`user_id`) USING BTREE;

ALTER TABLE `shence_data`
    ADD COLUMN `active` int(11) NULL DEFAULT NULL COMMENT '日活用户数' AFTER `total_users`;

INSERT INTO `banner_rule`(`id`, `ad_space`, `ad_type`, `ad_style`, `search_key`, `key`, `status`, `jt`, `app_type`, `total_name`) VALUES (55, '来电详情-退出-激励', '广点通-激励视频广告;快手-激励视频广告', '视频（不可跳过）', '来电详情退出||来电详情-退出', '来电详情退出|来电详情-退出,激励', 1, 1, 2, '来电详情-退出-激励');

