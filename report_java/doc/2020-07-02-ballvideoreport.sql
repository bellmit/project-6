CREATE TABLE `banner_data_user_behavior` (
                                             `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '汇总数据',
                                             `date` date NOT NULL DEFAULT '0000-00-00' COMMENT '日期',
                                             `show_value` double(10,0) unsigned NOT NULL COMMENT '注：没有除以日活;启动页-人均启动次数;视频底部banner广告-2.3版本以上用户人均首页列表观看视频数;视频结束广告-人均观看视频完播数;视频开始广告-人均观看视频数;视频中间广告-人均视频观看35%以上视频数;首页列表-人均观看视频数（来源是首页列表）;首页视频详情-人均观看视频数（来源是详情）;搜索结果广告-人均观看视频数（来源是搜索结果）;搜索页广告-人均观看视频数（来源是搜索页）;锁屏原生广告-人均观看视频数（来源是锁屏列表）;小视频列表-人均小视频菜单栏点击用户数;小视频详情-人均观看视频数（类型是小视频）',
                                             `app_name` char(20) NOT NULL DEFAULT '' COMMENT 'app 名称',
                                             `app_id` char(20) NOT NULL DEFAULT '' COMMENT 'app表自增 id',
                                             `client_id` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1安卓 ， 2ios',
                                             `ad_space` varchar(30) NOT NULL DEFAULT '' COMMENT '广告位置规则 总名称(total_name)',
                                             `app_type` tinyint(2) unsigned NOT NULL DEFAULT '1' COMMENT 'app类型:1视频，2炫来电',
                                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_date` (`date`) USING BTREE,
                                             KEY `idx_app_id` (`app_id`) USING BTREE,
                                             KEY `idx_ad_space` (`ad_space`) USING BTREE,
                                             KEY `idx_app_name` (`app_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据汇总-用户行为';

-- 2020/7/3
INSERT INTO `banner_rule` (`id`,`ad_space`,`ad_type`,`ad_style`,`search_key`,`key`,`status`,`jt`,`app_type`,`total_name`) VALUES (56,'详情页底部','广-原生;穿-信息流','模板渲染','详情页底部','详情页底部',1,1,1,'详情页底部');

