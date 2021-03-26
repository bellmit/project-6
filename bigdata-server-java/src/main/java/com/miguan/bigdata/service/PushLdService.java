package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.PushDto;
import com.miguan.bigdata.vo.PushResultVo;

import java.util.List;

/**
 * 来电自动推送
 */
public interface PushLdService {

    /**
     * 修改来电秀是否为视频库
     *
     * @param isPush   是否为push视频库。1是。0否
     * @param videoIds
     */
    void modifyIsPushTag(Integer isPush, String videoIds);

    /**
     * 同步push来电秀库的播放数（同步到mysql库）
     */
    void syncPushLdPlayCount();

    void syncAutoLdPushLog(String arrayList);

    /**
     * 同步来电秀分类
     */
    void syncLdVideoCat();

    /**
     * 统计（内容推送-新增用户）的数据
     *
     * @param packageName 包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     */
    void syncNewUser(String packageName, Integer triggerType);

    /**
     * 统计活跃用户数据
     *
     * @param packageName          包名
     * @param triggerType          触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param newActivityStartDays 最近一次活跃，开始天数
     * @param newActivityEndDays   最近一次活跃，结束天数
     */
    void syncActiveUser(String packageName, Integer triggerType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 统计不活跃用户数据
     *
     * @param packageName          包名
     * @param triggerType          触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param notActivityStartDays 不活跃累计，开始天数
     * @param notActivityEndDays   不活跃累计，结束天数
     */
    void syncNoActiveUser(String packageName, Integer triggerType, Integer notActivityStartDays, Integer notActivityEndDays);

    /**
     * 统计推送-新增用户
     *
     * @param packageName 包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送
     */
    void syncSignNewUser(String packageName, Integer triggerType, Integer userType);

    /**
     * 签到推送-新用户-未签到
     *
     * @param packageName 包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送
     */
    void syncNewUserNoSign(String packageName, int triggerType, int userType);

    /**
     * 签到推送-活跃用户-连续签到
     *
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    void syncActiveContinueSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 签到推送-活跃用户-昨日已签到-当日（0-20点）未签到
     *
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    void syncYesSignTodayNoSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 统计签到推送-活跃用户-昨日未签到(
     *
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    void syncYesNoSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 活动推送-新增用户
     *
     * @param packageName
     * @param triggerType
     * @param userType
     */
    void syncNewUserActivity(String packageName, int triggerType, int userType);

    /**
     * 活动推送-活跃用户
     *
     * @param packageName
     * @param triggerType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    void syncOldUser(String packageName, int triggerType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 统计推送-活跃用户
     */
    void syncSignActiveUser(String packageName, Integer triggerType, Integer userType, Integer newActivityStartDays, Integer newActivityEndDays);

    /**
     * 统计推送-不活跃用户
     */
    void syncSignNoActiveUser(String packageName, Integer triggerType, Integer userType, Integer notActivityStartDays, Integer notActivityEndDays);

    /**
     * 自动推送来电接口
     *
     * @param pushId    推送ID
     * @param userType    用户类型
     * @param packageName app包名
     * @param dd          日期：yyyy-MM-dd
     * @param pageNum     页码
     * @param pageSize    每页记录数
     * @return
     */
    List<PushResultVo> findLdAutoPushList(Integer pushId, Integer userType, String packageName, String dd, Integer pageNum, Integer pageSize);
    List<PushResultVo> findLdAutoPushList(PushDto pushDto);

    /**
     * @param type    统计类型，1--来电秀被收藏的次数，2--来电秀被使用次数
     * @param videoId
     * @return
     */
    Integer countVideoNum(int type, int videoId);

    /**
     * 从mysql来电库中的videos中同步来电秀数据到clickhouse的ld_video_info中
     */
    void syncLdVideoInfo();
}
