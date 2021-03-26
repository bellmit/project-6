package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.miguan.reportview.dto.UserContentDetailDto;
import com.miguan.reportview.dto.VideoSectionDataDto;
import com.miguan.reportview.entity.FirstVideos;
import com.miguan.reportview.vo.FirstVideosStaVo;
import com.miguan.reportview.vo.FirstVideosVo;
import com.miguan.reportview.vo.LdVideosVo;
import com.miguan.reportview.vo.UserContentDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页视频源列表(first_videos)数据Mapper
 *
 * @author zhongli
 * @since 2020-08-07 19:39:43
 * @description
 */
@Mapper
public interface FirstVideosMapper extends BaseMapper<FirstVideos> {

    List<FirstVideosStaVo> staAll();

    List<FirstVideosStaVo> staAdd(Map<String, Object> params);

    List<FirstVideosStaVo> staoffline(Map<String, Object> params);

    List<FirstVideosStaVo> staNewOnlineVideo(Map<String, Object> params);

    List<FirstVideosStaVo> staNewOfflineVideo(Map<String, Object> params);

    List<FirstVideosStaVo> staOlineVideo(Map<String, Object> params);

    List<FirstVideosStaVo> staNewCollectVideo(Map<String, Object> params);

    @DS("clickhouse")
    String findMaxVideoUpdatedTime();

    Integer countNewVideos(@Param("maxUpdateTime") String maxUpdateTime);

    /**
     * 根据更新时间，增量查询视频数据
     * @return
     */
    List<FirstVideosVo> queryNewVideos(Map<String, Object> params);

    @DS("clickhouse")
    void deleteVideoInfoById(@Param("dataList") List<FirstVideosVo> dataList);

    @DS("clickhouse")
    void batchInsertUpdateVideo(@Param("dataList") List<FirstVideosVo> dataList);

    @DS("clickhouse")
    void deleteVideoDetail(@Param("day") String day);

    /**
     * 同步视频明细数据
     * @param day
     */
    @DS("clickhouse")
    void batchSaveVideoDetail(@Param("day") String day);

    /**
     * 查询视频明细数据
     * @param params
     * @return
     */
    @DS("clickhouse")
    Page<UserContentDetailDto> findVideoDetailList(Map<String, Object> params);

    /**
     * 视频明细数据汇总数据
     * @param params
     * @return
     */
    @DS("clickhouse")
    UserContentDetailDto totalVideoDetail(Map<String, Object> params);

    /**
     * 视频区间汇总数据
     * @param params
     * @return
     */
    @DS("clickhouse")
    List<VideoSectionDataDto> findVideoSectionList(Map<String, Object> params);

    @DS("clickhouse")
    String findMaxLdVideoUpdatedTime();

    @DS("ld-db")
    Integer countNewLdVideos(@Param("maxUpdateTime") String maxUpdateTime);

    /**
     * 根据更新时间，增量查询来电秀数据
     * @return
     */
    @DS("ld-db")
    List<LdVideosVo> queryNewLdVideos(Map<String, Object> params);


    @DS("clickhouse")
    void deleteLdVideoInfoById(@Param("dataList") List<LdVideosVo> dataList);

    @DS("clickhouse")
    void batchInsertUpdateLdVideo(@Param("dataList") List<LdVideosVo> dataList);
}
