package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.PushDto;
import com.miguan.bigdata.dto.PushVideoDto;
import com.miguan.bigdata.vo.PushResultVo;

import java.util.List;

/**
 * 自动推送
 */
public interface PushVideoService {
    /**
     * 根据视频id查询当天的视频播放数、完播数
     *
     * @param videoIds
     * @return
     */
    List<PushVideoDto> findPushVideosInfo(List<String> videoIds);

    /**
     * 新增或删除push视频库
     *
     * @param type     1--新增，2--删除
     * @param videoIds
     */
    void saveAndDeletePushVideos(Integer type, List<String> videoIds);

    void syncAutoPushLog(String arrayList);

    /**
     * 同步push视频库视频的播放数，有效播放数，完播数
     */
    void syncPushVideos();

    /**
     * 查询出push推送的用户
     *
     * @param userType    用户类型
     * @param packageName 包名
     * @param activityDay 活跃天数范围
     * @param triggerType 触发类型  1：当天，2：明天
     */
    void batchSavePushUser(Integer userType, String packageName, String activityDay, Integer triggerType);

    /**
     * 生成用户推送结果表
     *
     * @param userType 用户类型
     * @param dd       日期
     */
    void batchStaPushVidosResult(Integer userType, String dd);

    /**
     * 自动推送视频接口
     *
     * @param pushId    推送ID
     * @param userType    用户类型
     * @param packageName app包名
     * @param dd          日期：yyyy-MM-dd
     * @param pageNum     页码
     * @param pageSize    每页记录数
     * @return
     */
    List<PushResultVo> findAutoPushList(Integer pushId, Integer userType, String packageName, String dd, Integer pageNum, Integer pageSize);
    List<PushResultVo> findAutoPushList(PushDto pushDto);

    void syncVideoInfo();

    /**
     * 汇总视频明细数据到clickhouse的video_detail表中
     * @param day
     */
    void syncVideoDetail(String day);
}
