ALTER TABLE banner_rule
    MODIFY COLUMN `ad_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '广告位类型' AFTER `ad_space`,
    MODIFY COLUMN `ad_style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '广告位样式' AFTER `ad_type`,
    ADD COLUMN `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '匹配的key(java端使用)' AFTER `search_key`;

UPDATE banner_rule set `key` = REPLACE(search_key,'-','|') WHERE app_type=1;
UPDATE banner_rule set `key` = REPLACE(search_key,'||','|') WHERE app_type=2;
UPDATE banner_rule SET `key` = '来电详情,&^设置全屏广告位,&^解锁,&^弹窗,&^退出' WHERE `id` = 28;
UPDATE banner_rule SET `key` = '来电详情退出|来电详情-退出,type=激励视频' WHERE `id` = 32;
UPDATE banner_rule SET `key` = '来电详情退出|来电详情-退出,type=全屏视频' WHERE `id` = 33;


CREATE TABLE `shence_data` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `date` varchar(10) DEFAULT NULL COMMENT '日期：yyyy-MM-dd',
                               `new_users` int(11) DEFAULT NULL COMMENT '新增用户数',
                               `total_users` int(11) DEFAULT NULL COMMENT '总用户数',
                               `app_type` smallint(2) DEFAULT NULL COMMENT 'app类型：1--视频，2--来电',
                               `app_id` int(11) DEFAULT NULL COMMENT 'app表的id',
                               `app_name` varchar(60) DEFAULT NULL COMMENT '应用名称',
                               `client_id` smallint(11) DEFAULT '1' COMMENT '客户端：1安卓 2ios',
                               `created_at` datetime DEFAULT NULL COMMENT '创建时间',
                               PRIMARY KEY (`id`),
                               KEY `date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='神策数据表';

ALTER  TABLE  `umeng_data`  ADD  INDEX date (  `date`  )