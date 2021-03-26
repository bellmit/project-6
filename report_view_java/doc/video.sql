-- 2020/8/7
ALTER TABLE `first_videos`
    ADD INDEX `idx_offline_time` (`offline_time`) USING BTREE;