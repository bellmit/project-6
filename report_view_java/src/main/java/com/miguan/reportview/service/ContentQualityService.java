package com.miguan.reportview.service;

import com.github.pagehelper.PageInfo;
import com.miguan.reportview.dto.ContentQualityDto;
import com.miguan.reportview.dto.OnlineDataContrastDto;
import com.miguan.reportview.dto.OnlineVideoDto;

/**
 * @Description 内容质量评估service
 * @Author zhangbinglin
 * @Date 2020/9/21 14:11
 **/
public interface ContentQualityService {

    /**
     * 线上短视频库列表
     *
     * @param video_id        视频id
     * @param video_title     视频标题
     * @param video_source    内容源
     * @param cat_id          分类id
     * @param gather_id       合集id
     * @param play_effect       播放效果
     * @param startUpdateDate 更新时间（start）
     * @param endUpdateDate   更新时间（end）
     * @param startOnlineDate 上线日期（start）
     * @param endOnlineDate   上线日期（end）
     * @param startStaDate    统计日期（start）
     * @param endStaDate      统计日期（end）
     * @param orderByField    排序字段
     * @param pageNum         页码
     * @param pageSize        每页记录数
     * @return
     */
    PageInfo<OnlineVideoDto> listOnlineVideo(String video_id,
                                             String video_title,
                                             String video_source,
                                             String package_name,
                                             Long cat_id,
                                             Long gather_id,
                                             Integer play_effect,
                                             Integer sensitive,
                                             String startUpdateDate,
                                             String endUpdateDate,
                                             String startOnlineDate,
                                             String endOnlineDate,
                                             String startStaDate,
                                             String endStaDate,
                                             String orderByField,
                                             int pageNum,
                                             int pageSize);

    /**
     * 线上短视频-数据对比
     *
     * @param video_id     视频id
     * @param startStaDate 统计日期（start）
     * @param endStaDate   统计日期（end）
     * @param pageNum      页码
     * @param pageSize     每页记录数
     * @return
     */
    PageInfo<OnlineDataContrastDto> listOnlineDataContrast(Long video_id, String startStaDate, String endStaDate, int pageNum, int pageSize);

    /**
     * 内容质量评估报表
     *
     * @param video_source    内容源
     * @param cat_id          分类id
     * @param startOnlineDate 进文时间（start）
     * @param endOnlineDate   进文时间（end）
     * @param startStaDate    统计日期（start）
     * @param endStaDate      统计日期（end）
     * @param orderByField    排序字段
     * @param pageNum         页码
     * @param pageSize        每页记录数
     * @return
     */
    PageInfo<ContentQualityDto> listContentQuality(String video_source,String package_name, Long cat_id, String startOnlineDate, String endOnlineDate, String startStaDate,
                                                   String endStaDate, String orderByField, int pageNum, int pageSize);

}
