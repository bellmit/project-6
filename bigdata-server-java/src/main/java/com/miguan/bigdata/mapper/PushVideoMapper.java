package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.dto.PushVideoDto;
import com.miguan.bigdata.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PushVideoMapper {

    /**
     * 查询push视频库的最大更新时间
     *
     * @return
     */
    @DS("clickhouse")
    List<PushVideoDto> findPushVideosInfo(@Param("videoIds") List<String> videoIds);

    /**
     * 批量新增push视频库
     *
     * @param videoIds
     */
    @DS("clickhouse")
    void batchSavePushVideos(@Param("videoIds") List<String> videoIds);

    /**
     * 批量删除push视频库
     *
     * @param videoIds
     */
    @DS("clickhouse")
    void deletePushVideos(@Param("videoIds") List<String> videoIds);

    /**
     * 删除推送用户
     *
     * @param params
     */
    @DS("clickhouse")
    void deletePushUser(Map<String, Object> params);

    /**
     * 删除推送用户
     */
    @DS("clickhouse")
    void deleteOldPushUser(@Param("dt") Integer dt);

    /**
     * 新用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为
     *
     * @param params
     */
    @DS("clickhouse")
    void batchSaveNewUserNoPlay(Map<String, Object> params);

    /**
     * 新用户（激活当天) 当日产生播放行为
     *
     * @param params
     */
    @DS("clickhouse")
    void batchSaveNewUserPlay(Map<String, Object> params);

    /**
     * 老用户（激活次日以后） 当日产生播放行为
     *
     * @param params
     */
    @DS("clickhouse")
    void batchSaveOldUserPlay(Map<String, Object> params);

    /**
     * 老用户（激活次日以后） 当日未产生播放行为
     *
     * @param params
     */
    @DS("clickhouse")
    void batchSaveOldUserNoPlay(Map<String, Object> params);

    /**
     * 不活跃用户（未启动天数>=30天）
     *
     * @param params
     */
    @DS("clickhouse")
    void batchSaveOldUserNoActive(Map<String, Object> params);

    /**
     * 同步自动推送记录
     *
     * @param list
     */
    @DS("clickhouse")
    void syncAutoPushLog(List<Map<String, Object>> list);

    @DS("clickhouse")
    Integer countPushVideos();

    @DS("clickhouse")
    List<PushVideoVo> listPushVideos(Map<String, Object> params);

    /**
     * 删除mysql的push视频库的中间表
     */
    @DS("xy-db")
    void deletePushVideosMid();

    /**
     * 同步push视频库的中间表
     */
    @DS("xy-db")
    void batchSavePushVideosMid(List<PushVideoVo> list);

    /**
     * 把push视频库中间表的数据更新到push视频表中
     */
    @DS("xy-db")
    void updatePushVideosNums();

    /**
     * 查询自动推送配置信息
     *
     * @return
     */
    @DS("npush-db")
    List<PushConfigVo> findPushConfig();

    /**
     * 查询自动推送配置信息(用户行为类型)
     *
     * @return
     */
    @DS("xy-db")
    List<Integer> findPushConfigUserType();

    /**
     * 生成用户推送结果表
     *
     * @param params
     */
    @DS("clickhouse")
    void batchStaPushVidosResult(Map<String, Object> params);

    @DS("clickhouse")
    void deletePushVideosResult(Map<String, Object> params);


    @DS("clickhouse")
    void deleteOldPushVideosResult(@Param("dd") String dd);

    @DS("clickhouse")
    void updatePushVideosResultTag(Map<String, Object> params);

    @DS("data-server")
    List<PushResultVo> findPushVideosResult(Map<String, Object> params);

    @DS("data-server")
    void deleteAutoPushUserVideo(Map<String, Object> params);

    @DS("clickhouse")
    void insertAutoPushUserVideo(Map<String, Object> params);

    @DS("data-server")
    List<Map<String, Object>> staOldVideoItem(@Param("dd") String dd);

    @DS("data-server")
    void optimizeTable(@Param("tableName") String tableName);

    @DS("data-server")
    int isExistAutoPushTable(@Param("tableName") String tableName);

    @DS("data-server")
    List<String> findOldAutoPushTable(@Param("dt") String dt);

    @DS("data-server")
    void createAutoPushTable(@Param("tableName") String tableName);

    @DS("data-server")
    void dropAutoPushTable(@Param("tableName") String tableName);

    String findMaxVideoUpdatedTime();

    @DS("xy-db")
    Integer countNewVideos(@Param("maxUpdateTime") String maxUpdateTime);

    /**
     * 根据更新时间，增量查询视频数据
     * @return
     */
    @DS("xy-db")
    List<FirstVideosPushVo> queryNewVideos(Map<String, Object> params);

    void deleteVideoInfoById(@Param("dataList") List<FirstVideosPushVo> dataList);

    void batchInsertUpdateVideo(@Param("dataList") List<FirstVideosPushVo> dataList);

    void deleteVideoDetail(@Param("day") String day);
    /**
     * 同步视频明细数据
     * @param day
     */
    void batchSaveVideoDetail(@Param("day") String day);

}
