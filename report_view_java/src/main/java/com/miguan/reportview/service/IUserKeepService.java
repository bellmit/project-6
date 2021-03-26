package com.miguan.reportview.service;

import com.miguan.reportview.dto.YesterDayUserKeepDto;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.vo.UserKeepVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频数据宽表 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface IUserKeepService {

    void delete(int sd);

    List<UserKeepVo> getData(List<String> valueTypes,
                             Boolean isNew,
                             List<String> appPackages,
                             List<String> appVersions,
                             List<String> pChannelIds,
                             List<String> channelIds,
                             int appType,
                             String startDate,
                             String endDate);

    List<RpUserKeep> getTableData(Boolean isNew,
                                  List<String> appPackages,
                                  List<String> appVersions,
                                  List<String> pChannelIds,
                                  List<String> channelIds,
                                  int appType,
                                  String startDate,
                                  String endDate);

    int deleteBySd(Integer sd, int appType);

    int insert(RpUserKeep rpUserKeep);
    int insertBatch(List<RpUserKeep> dataList);

    public int insertLdBatch(List<RpUserKeep> dataList);

    RpUserKeep findByDto(Map<String, Object> yesterDayUserKeepDto);

    int updateUserAndKeep1ById(Long id, Integer user, Integer keep1);

    /**
     * 同步来电指定日期的历史留存数据
     * @param sd 留存日期
     */
    void syncLdUserKeepData(Date sd);

    List<UserKeepVo> getNewUserKeep(Boolean isNew,
                                    List<String> appPackages,
                                    List<String> appVersions,
                                    List<String> pChannelIds,
                                    List<String> channelIds,
                                    List<String> groups,
                                    int appType,
                                    String startDate,
                                    String endDate);
}
