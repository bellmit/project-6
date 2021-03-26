-- 2020/7/7
ALTER TABLE `banner_data_user_behavior`
    ADD COLUMN `active` int(11) NOT NULL DEFAULT 0 COMMENT '日活' AFTER `show_value`;

ALTER TABLE `banner_data_from_adv`
    ADD COLUMN `app_id` int(11) NULL DEFAULT NULL COMMENT 'app表的id' AFTER `cut_app_name`,
    ADD COLUMN `sort_value` int(11) NULL DEFAULT NULL COMMENT '重新排序值' AFTER `option_value`;


UPDATE banner_data_from_adv t SET t.app_id = (SELECT id FROM app WHERE name = t.cut_app_name);

UPDATE banner_data_from_adv t SET t.sort_value = (
    SELECT aa.rank FROM (
                            SELECT @rownum:=@rownum+1 AS rownum,IF(@pa=ta.uid,@rank:=@rank+1,@rank:=1) AS rank,ta.option_value, @pa:=ta.uid as uuid, ta.ad_id FROM
                                (SELECT concat(t.cut_app_name,t.client_id,t.total_name,t.date) as uid,t.*  FROM banner_data_from_adv t
                                WHERE computer = 2
                                ORDER BY uid, option_value DESC) ta,(SELECT @rank:=0,@rownum:=0,@pa=NULL) tr ORDER BY date,ta.uid,rank

                        ) aa
    WHERE concat(t.cut_app_name,t.client_id,t.total_name,t.date) = aa.uuid and t.computer = 2 and aa.ad_id = t.ad_id
);

UPDATE banner_data_from_adv t SET t.sort_value = t.option_value WHERE t.computer = 1;

UPDATE banner_data_from_adv set app_package = 'com.mg.phonecall' WHERE app_package = 'xld';
UPDATE banner_data_from_adv set app_package = 'com.xm98.grapefruit' WHERE cut_app_name = '茜柚视频' and client_id = 2;

-- 2020/7/10
UPDATE banner_rule SET `key` = '激励视频解锁|激励视频广告位', search_key = '激励视频解锁-激励视频广告位' WHERE id = 54;