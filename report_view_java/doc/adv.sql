-- 2020/8/3
ALTER TABLE `ad_advert_position`
    ADD COLUMN `report_show_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报表展示名称' AFTER `total_name`;

update ad_advert_position set report_show_name='4G浏览提示广告' where position_type = '4GvideoTips';
update ad_advert_position set report_show_name='首页视频详情' where position_type = 'detailPage';
update ad_advert_position set report_show_name='详情页底部' where position_type = 'detailPageBottom';
update ad_advert_position set report_show_name='视频开始广告' where position_type = 'firstLoadPosition';
update ad_advert_position set report_show_name='首页浮窗广告' where position_type = 'floatingDeblocking';
update ad_advert_position set report_show_name='小游戏全屏广告' where position_type = 'GameFullScreen';
update ad_advert_position set report_show_name='小游戏激励视频' where position_type = 'GameIncentiveVideo';
update ad_advert_position set report_show_name='小游戏列表信息流' where position_type = 'GameListDeblocking';
update ad_advert_position set report_show_name='小游戏列表插屏' where position_type = 'GameListTableScreen';
update ad_advert_position set report_show_name='小游戏个人中心自定义' where position_type = 'GameMyCustomer';
update ad_advert_position set report_show_name='激励视频解锁' where position_type = 'incentiveVideoPosition';
update ad_advert_position set report_show_name='锁屏原生广告' where position_type = 'lockAppScreenDeblocking';
update ad_advert_position set report_show_name='锁屏H5广告' where position_type = 'lockH5ScreenDeblocking';
update ad_advert_position set report_show_name='锁屏广告' where position_type = 'lockScreenDeblocking';
update ad_advert_position set report_show_name='启动页' where position_type = 'openScreenPageIos';
update ad_advert_position set report_show_name='首页列表' where position_type = 'personalPage';
update ad_advert_position set report_show_name='搜索结果广告' where position_type = 'searchDetailPosition';
update ad_advert_position set report_show_name='搜索页广告' where position_type = 'searchMenuPosition';
update ad_advert_position set report_show_name='视频中间广告' where position_type = 'secondLoadPosition';
update ad_advert_position set report_show_name='小视频详情' where position_type = 'smallVideoDetailsDeblocking';
update ad_advert_position set report_show_name='小视频列表' where position_type = 'smallVideoListDeblocking';
update ad_advert_position set report_show_name='视频底部banner广告' where position_type = 'videoBannerDeblocking';
update ad_advert_position set report_show_name='视频结束广告' where position_type = 'videoEndDeblocking';
update ad_advert_position set report_show_name='专辑详情列表' where position_type = 'albumDetailListPosition';
update ad_advert_position set report_show_name='专辑解锁' where position_type = 'albumincentiveVideoPosition';
update ad_advert_position set report_show_name='专辑首页列表' where position_type = 'albumInformationFlowPosition';
update ad_advert_position set report_show_name='专辑页插屏' where position_type = 'albumScreenPosition';
update ad_advert_position set report_show_name='退出弹窗' where position_type = 'exit_dialog_draw';
update ad_advert_position set report_show_name='视频分类插屏' where position_type = 'home_tab_tableqlaque';
update ad_advert_position set report_show_name='金币弹窗' where position_type = 'taskGoldPopAd';
update ad_advert_position set report_show_name='金币弹窗翻倍' where position_type = 'taskGoldPopDoubleAd';
update ad_advert_position set report_show_name='补签激励' where position_type = 'taskSignRewardAd';
update ad_advert_position set report_show_name='看视频赚金币' where position_type = 'taskWatchRewardAd';
update ad_advert_position set report_show_name='启动插屏' where position_type = 'openScreenPage';
update ad_advert_position set report_show_name='首页列表插屏' where position_type = 'personalPageScreen';
update ad_advert_position set report_show_name='推荐页视频' where position_type = 'recommendVideo';
update ad_advert_position set report_show_name='视频结束贴片' where position_type = 'videoEndPaste';
update ad_advert_position set report_show_name='小游戏banner广告' where position_type = 'GameBanner';
update ad_advert_position set report_show_name='小游戏插屏广告' where position_type = 'GameTableScreen';
update ad_advert_position set report_show_name='分享操作广告' where position_type = 'shareVideoDeblocking';
update ad_advert_position set report_show_name='来电分类解锁' where position_type = 'callClassificationDeblocking';
update ad_advert_position set report_show_name='来电详情' where position_type = 'callDetails';
update ad_advert_position set report_show_name='来电详情-解锁' where position_type = 'callDetailsDeblocking';
update ad_advert_position set report_show_name='来电详情-弹窗' where position_type = 'callDetailsPopupWindows';
update ad_advert_position set report_show_name='来电详情-退出' where position_type = 'callDetailsQuit';
update ad_advert_position set report_show_name='通话结束广告' where position_type = 'callEndAdv';
update ad_advert_position set report_show_name='通话结束关闭弹窗' where position_type = 'callEndPopupAdv';
update ad_advert_position set report_show_name='关闭通话结束弹窗' where position_type = 'callEndToDayNoMoreAdv';
update ad_advert_position set report_show_name='来电列表' where position_type = 'callList';
update ad_advert_position set report_show_name='立即领取广告' where position_type = 'detailPageInterval';
update ad_advert_position set report_show_name='详情页间隔广告' where position_type = 'detailPageSpaceAd';
update ad_advert_position set report_show_name='活动中奖弹窗广告' where position_type = 'eventWinningPopUp';
update ad_advert_position set report_show_name='退出页' where position_type = 'exitPage';
update ad_advert_position set report_show_name='来电强制解锁广告' where position_type = 'forcedToUnlockPosition';
update ad_advert_position set report_show_name='试听铃声广告' where position_type = 'listenToTheRingTone';
update ad_advert_position set report_show_name='启动页' where position_type = 'openScreenPageAndroid';
update ad_advert_position set report_show_name='个人来电秀广告' where position_type = 'personalLaidianShowAdv';
update ad_advert_position set report_show_name='铃声分类解锁广告' where position_type = 'ringClassificationUnlocking';
update ad_advert_position set report_show_name='铃声设置成功关闭弹窗' where position_type = 'ringSetSuccessPopUpWindow';
update ad_advert_position set report_show_name='解锁设置铃声' where position_type = 'ringToneSetAdv';
update ad_advert_position set report_show_name='铃声设置成功广告' where position_type = 'ringToneSetSuccess';
update ad_advert_position set report_show_name='设置来电秀成功关闭弹窗' where position_type = 'setupSucPopupAdv';
update ad_advert_position set report_show_name='来电详情-设置全屏' where position_type = 'setWallpaperAdv';
update ad_advert_position set report_show_name='签到成功弹窗' where position_type = 'signSuccessPopUpWindow';
update ad_advert_position set report_show_name='获取华为P40抽奖次数' where position_type = 'timesOfWinningHuaweiP40Lottery';
update ad_advert_position set report_show_name='获取抽奖次数' where position_type = 'timesOfWinningLottery';
update ad_advert_position set report_show_name='锁屏壁纸解锁广告' where position_type = 'wallpaperUnlockAdv';


-- 2020/8/7
UPDATE ad_advert_position set total_name = report_show_name WHERE total_name is null and report_show_name is not null;
UPDATE ad_advert_position set report_show_name = total_name WHERE total_name != report_show_name;

