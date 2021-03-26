package com.miguan.reportview.service;

import com.github.pagehelper.PageInfo;
import com.miguan.reportview.dto.LdUserContentDto;
import com.miguan.reportview.dto.UserContentDetailDto;
import com.miguan.reportview.dto.UserContentDto;
import com.miguan.reportview.dto.VideoSectionDataDto;

import java.util.List;

/**
 * <p>
 * 视频数据宽表 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface IUserContentService {

    List<UserContentDto> getData(Boolean isNew,
                                 List<String> appPackages,
                                 List<String> appVersions,
                                 List<String> pChannelIds,
                                 List<String> channelIds,
                                 List<String> catIds,
                                 Integer incentiveTag,
                                 List<String> videoSources,
                                 List<String> groups,
                                 String startDate,
                                 String endDate);

    List<LdUserContentDto> getLdData(List<String> appVersions,
                                     Boolean isNewApp,
                                     List<String> channelIds,
                                     List<String> pChannelIds,
                                     List<String> videoTypes,
                                     List<String> groups,
                                     String startDate,
                                     String endDate);

    /**
     * 视频曝光区间明显汇总列表
     * @param catIds 分类id
     * @param sectionType 区间类型，1=6区间，2=9区间
     * @param day 日期，yyyy-MM-dd
     * @return
     */
    List<VideoSectionDataDto> findVideoSectionList(List<Integer> catIds, Integer sectionType, String day);

    /**
     * 视频明细数据
     * @param appPackages 应用
     * @param appVersions 版本
     * @param catId 分类id
     * @param sectionType 自定义曝光区间：1=6个区间，2=9个区间
     * @param nowOnlineType 是否当天上线：1=当天，2=全部
     * @param nowJwType 是否当进文：1=当天，2=全部
     * @param dateType 时间类型：1=行为日期，2=上线日期，3=进文日期
     * @param day 日期; 日期格式: yyyy-MM-dd
     * @param orderByField 排序字段,格式:字段名+空格+升降序标识
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param startRow 分页起始记录数
     * @param endRow 分页结束记录数(startRow和endRow参数，只能与pageNum和pageSize两参数任选一种)
     * @return
     */
    PageInfo<UserContentDetailDto> findVideoDetailList(List<String> appPackages,
                                                       List<String> appVersions,
                                                       List<Integer> catId,
                                                       Integer sectionType,
                                                       Integer nowOnlineType,
                                                       Integer nowJwType,
                                                       Integer dateType,
                                                       String day,
                                                       Integer isIncentive,
                                                       String orderByField,
                                                       Integer pageNum,
                                                       Integer pageSize,
                                                       Integer startRow,
                                                       Integer endRow);
}
