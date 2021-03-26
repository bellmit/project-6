package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.vo.LdVideosVo;
import com.miguan.bigdata.vo.PushResultVo;
import com.miguan.bigdata.vo.PushLdConfigVo;
import com.miguan.bigdata.vo.PushVideoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PushLdMapper {

    /**
     * 修改是否是push视频库标识
     *
     * @param params
     */
    @DS("clickhouse")
    void modifyIsPushTag(Map<String, Object> params);

    @DS("clickhouse")
    Integer countPushLd();

    @DS("clickhouse")
    List<PushVideoVo> listPushLd(Map<String, Object> params);

    @DS("ld-db")
    void deletePushLdMid();

    @DS("ld-db")
    void batchSavePushLdMid(@Param("list") List<PushVideoVo> list);

    @DS("ld-db")
    void updatePushLdPlayCount();

    /**
     * 同步自动推送记录
     *
     * @param list
     */
    @DS("clickhouse")
    void syncAutoLdPushLog(List<Map<String, Object>> list);

    /**
     * 查询来电秀分类
     *
     * @return
     */
    @DS("ld-db")
    List<Map<String, Object>> findLdVideosCatList();

    @DS("clickhouse")
    void deleteCkLdVideoCat();

    @DS("clickhouse")
    void syncLdVideoCat(@Param("list") List<Map<String, Object>> list);

    /**
     * 获取来电自动推送配置
     *
     * @return
     */
    @DS("npush-db")
    List<PushLdConfigVo> queryLdAutoConfig();

    /**
     * 删除来电push用户数据
     *
     * @param params
     */
    @DS("clickhouse")
    void deleteLdPushUser(Map<String, Object> params);

    /**
     * 统计 新增用户-未设置来电秀的用户数据
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNewUserNoSetVideo(Map<String, Object> params);

    /**
     * 统计 新增用户-已设置来电秀 的用户数据
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNewUserSetVideo(Map<String, Object> params);

    /**
     * 统计 新增用户-未设置铃声 的用户数据
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNewUserNoSetRing(Map<String, Object> params);

    /**
     * 统计 内容推送-活跃用户
     *
     * @param params
     */
    @DS("clickhouse")
    void syncActiveVideos(Map<String, Object> params);

    /**
     * 统计 内容推送-不活跃用户
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNoActiveVideos(Map<String, Object> params);

    /**
     * 签到推送-新用户-未签到
     *
     * @param params
     */
    @DS("clickhouse")
    void insertNewUserNoSign(Map<String, Object> params);

    /**
     * 签到推送-活跃用户-连续签到
     *
     * @param params
     */
    @DS("clickhouse")
    void insertActiveContinueSign(Map<String, Object> params);

    /**
     * 签到推送-活跃用户-昨日已签到-当日（0-20点）未签到
     *
     * @param params
     */
    @DS("clickhouse")
    void insertYesSignTodayNoSign(Map<String, Object> params);

    /**
     * 签到推送-活跃用户-昨日未签到
     *
     * @param params
     */
    @DS("clickhouse")
    void insertYesNoSign(Map<String, Object> params);

    /**
     * 活动推送-新增用户
     *
     * @param params
     */
    @DS("clickhouse")
    void insertNewUserActivity(Map<String, Object> params);

    /**
     * 活动推送-活跃用户-今日抽奖次数=0
     *
     * @param params
     */
    @DS("clickhouse")
    void insertOldUserNoLuckDraw(Map<String, Object> params);

    /**
     * 活动推送-活跃用户-抽奖次数不等于0的用户
     *
     * @param params
     */
    @DS("clickhouse")
    void insertOldUserHavLuckDraw(Map<String, Object> params);

    /**
     * 统计 时间段内的新用户
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNewUser(Map<String, Object> params);


    /**
     * 统计 时间段内的活跃用户
     *
     * @param params
     */
    @DS("clickhouse")
    void syncActiveUser(Map<String, Object> params);

    /**
     * 统计 不活跃用户
     *
     * @param params
     */
    @DS("clickhouse")
    void syncNoActiveUser(Map<String, Object> params);

    @DS("data-server")
    List<PushResultVo> findLdAutoPushList(Map<String, Object> params);

    @DS("clickhouse")
    List<PushResultVo> findLdAutoPushUserList(Map<String, Object> params);

    /**
     * 删除推送用户
     */
    @DS("clickhouse")
    void deleteOldPushLdUser(@Param("dd") String dd);

    /**
     * 查询某个来电秀被收藏的次数
     *
     * @param videoId
     * @return
     */
    @DS("clickhouse")
    Integer countVideoCollect(Integer videoId);

    /**
     * 查询某个视频被设置来电秀的次数
     *
     * @param videoId
     * @return
     */
    @DS("clickhouse")
    Integer countVideoTabConfirm(Integer videoId);

    @DS("data-server")
    void delLdAutoPushUserVideo(Map<String, Object> params);

    @DS("clickhouse")
    void insertLdAutoPushUserVideo(Map<String, Object> params);

    @DS("data-server")
    void delOldLdAutoPushUserVideo(@Param("dd") String dd);

    String findMaxLdVideoUpdatedTime();

    @DS("laidian-db")
    Integer countNewLdVideos(@Param("maxUpdateTime") String maxUpdateTime);

    /**
     * 根据更新时间，增量查询来电秀数据
     * @return
     */
    @DS("laidian-db")
    List<LdVideosVo> queryNewLdVideos(Map<String, Object> params);

    void deleteLdVideoInfoById(@Param("dataList") List<LdVideosVo> dataList);

    void batchInsertUpdateLdVideo(@Param("dataList") List<LdVideosVo> dataList);
}
