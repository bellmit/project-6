package com.miguan.reportview.common.enmus;

/**
 * 来电推送用户类型
 */
public enum LdPushUserEnmu {
    /**
     *
     */
    CONTENT_PUSH_NEWUSER_A(11001, "内容推送-新增用户-未设置来电秀-未开通权限-已浏览来电秀",true),
    CONTENT_PUSH_NEWUSER_B(11002, "内容推送-新增用户-未设置来电秀-未开通权限-未浏览来电秀 ",true),
    CONTENT_PUSH_NEWUSER_C(11004, "内容推送-新增用户-未设置来电秀-已开通权限-已浏览来电秀",true),
    CONTENT_PUSH_NEWUSER_D(11005, "内容推送-新增用户-未设置来电秀-已开通权限-未浏览来电秀",true),
    CONTENT_PUSH_NEWUSER_E(12001, "内容推送-新增用户-已设置来电秀-未设置壁纸",true),
    CONTENT_PUSH_NEWUSER_F(12002, "内容推送-新增用户-已设置来电秀-未设置锁屏",true),
    CONTENT_PUSH_NEWUSER_G(13001, "内容推送-新增用户-未设置铃声-已浏览铃声",false),
    CONTENT_PUSH_ACTIVE_A(21007, "内容推送-活跃用户-未设置来电秀-未开通权限",true),
    CONTENT_PUSH_ACTIVE_B(21003, "内容推送-活跃用户-未设置来电秀-已开通权限",true),
    CONTENT_PUSH_ACTIVE_C(22001, "内容推送-活跃用户-已设置来电秀-未设置壁纸",true),
    CONTENT_PUSH_ACTIVE_D(22002, "内容推送-活跃用户-已设置来电秀-未设置锁屏",true),
    CONTENT_PUSH_ACTIVE_E(23002, "内容推送-活跃用户-未设置铃声",false),
    CONTENT_PUSH_NOT_ACTIVE_A(31005, "内容推送-不活跃用户-未设置来电秀",false),
    CONTENT_PUSH_NOT_ACTIVE_B(32001, "内容推送-不活跃用户-已设置来电秀-未设置壁纸",false),
    CONTENT_PUSH_NOT_ACTIVE_C(32002, "内容推送-不活跃用户-已设置来电秀-未设置锁屏",false),
    CONTENT_PUSH_NOT_ACTIVE_E(33001, "内容推送-不活跃用户-未设置铃声-已浏览铃声",false),
    SIGN_PUSH_NEWUSER_A(15003, "签到推送-新增用户-未签到",false),
    SIGN_PUSH_ACTIVE_A(24001, "签到推送-活跃用户-连续6天签到",false),
    SIGN_PUSH_ACTIVE_B(24002, "签到推送-活跃用户-连续2天签到",false),
    SIGN_PUSH_ACTIVE_C(25001, "签到推送-活跃用户-昨日已签到",false),
    SIGN_PUSH_ACTIVE_D(25002, "签到推送-活跃用户-昨日未签到",false),
    ACTIVITY_PUSH_NEWUSER_A(16001, "活动推送-新增用户-未访问活动页面-18点前新增的用户（0-18点）",false),
    ACTIVITY_PUSH_NEWUSER_B(16002, "活动推送-新增用户-未访问活动页面-18点后新增的用户（18:01-23:59）",false),
    ACTIVITY_PUSH_ACTIVE_A(26003, "活动推送-活跃用户-抽奖次数不等于0的用户",false),
    ACTIVITY_PUSH_ACTIVE_B(26004, "活动推送-活跃用户-今日抽奖次数=0",false),
    ACTIVITY_PUSH_ACTIVE_C(38001, "活动推送-不活跃用户",false);

    int code;
    String value;
    boolean needVideo;

    LdPushUserEnmu(int code, String value, boolean needVideo) {
        this.code = code;
        this.value = value;
        this.needVideo = needVideo;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public boolean isNeedVideo() {
        return needVideo;
    }

    public static boolean getIsNeedVideo(int code) {
        for (LdPushUserEnmu oneEnum : LdPushUserEnmu.values()) {
            if(oneEnum.getCode() == code) {
                return oneEnum.isNeedVideo();
            }
        }
        return false;
    }
}