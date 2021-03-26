-- 西柚库
ALTER TABLE `cl_user`
    ADD COLUMN `uuid` varchar(32) NULL COMMENT '用户uuid' AFTER `used_gold`,
    ADD UNIQUE INDEX `idx_uuid`(`uuid`) USING BTREE;

