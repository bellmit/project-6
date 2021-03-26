CREATE TABLE `cl_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(80) DEFAULT '' COMMENT '设备ID',
  `state` varchar(4) DEFAULT '10' COMMENT '状态（10 正常 20 禁用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `huawei_token` varchar(255) DEFAULT '' COMMENT '华为对设备的唯一标识',
  `vivo_token` varchar(100) DEFAULT '' COMMENT 'vivo对设备的唯一标识',
  `oppo_token` varchar(100) DEFAULT '' COMMENT 'oppo对设备的唯一标识',
  `xiaomi_token` varchar(100) DEFAULT '' COMMENT '小米对设备的唯一标识',
  `app_type` varchar(50) DEFAULT 'xld' COMMENT '包名',
  `channel_id` varchar(50) DEFAULT NULL COMMENT '来源渠道号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_id` (`device_id`,`app_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户表';