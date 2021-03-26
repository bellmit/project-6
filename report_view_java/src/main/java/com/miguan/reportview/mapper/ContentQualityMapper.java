package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.miguan.reportview.dto.ContentQualityDto;
import com.miguan.reportview.dto.OnlineDataContrastDto;
import com.miguan.reportview.dto.OnlineVideoDto;
import com.miguan.reportview.dto.UserContentDetailDto;
import com.miguan.reportview.entity.AdAdvertCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 内容质量评估 Mapper 接口
 * </p>
 *
 */
public interface ContentQualityMapper {

    /**
     * 线上短视频库列表
     * @param params
     * @return
     */
    @DS("clickhouse")
    Page<OnlineVideoDto> listOnlineVideo(Map<String, Object> params);

    /**
     * 线上短视频-数据对比
     * @param params
     * @return
     */
    @DS("clickhouse")
    Page<OnlineDataContrastDto> listOnlineDataContrast(Map<String, Object> params);

    /**
     * 内容质量评估报表
     * @param params
     * @return
     */
    @DS("clickhouse")
    Page<ContentQualityDto> listContentQuality(Map<String, Object> params);

    /**
     * 所有分类所有视频来源的总曝光数
     * @param params
     * @return
     */
    @DS("clickhouse")
    Long totalShowCount(Map<String, Object> params);

    @DS("video-db")
    List<Map<String,Object>> listVideoSource();

}
