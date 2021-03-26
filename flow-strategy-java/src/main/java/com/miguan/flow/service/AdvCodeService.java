package com.miguan.flow.service;

import com.miguan.flow.dto.AdvertCodeDto;
import com.miguan.flow.dto.AdvertCodeParamDto;
import com.miguan.flow.dto.common.AbTestAdvParamsDto;
import com.miguan.flow.vo.common.PublicInfo;

import java.util.List;
import java.util.Map;

/**
 * @Description 广告代码位service
 **/
public interface AdvCodeService {

    /**
     * 获取广告代码位列表
     * @param queueVo AB测试vo
     * @param publicInfo 请求头参数
     * @param paramDto 接口参数
     * @return
     */
    List<AdvertCodeDto> advCodeInfoList(AbTestAdvParamsDto queueVo, PublicInfo publicInfo, AdvertCodeParamDto paramDto);

    /**
     * 获取广告位信息
     * @param positionType 广告位key
     * @param appPackage  包名
     * @param mobileType 手机类型1*应用端:1-ios，2-安卓
     * @return
     */
    List<AdvertCodeDto> positionInfoList(String positionType, String appPackage, String mobileType);

    /**
     * 统计多维度代码位ecpm
     * @param adIds  代码位列表
     * @param params 参数
     * @return
     */
    Map<String, Double> countMultiEcpm(List<String> adIds, Map<String, Object> params);
}
