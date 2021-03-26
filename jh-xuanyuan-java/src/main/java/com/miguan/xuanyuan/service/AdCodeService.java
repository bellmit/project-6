package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.dto.AdCodeDto;
import com.miguan.xuanyuan.dto.AdDataDto;
import com.miguan.xuanyuan.dto.AdvertCodeParamDto;
import com.miguan.xuanyuan.dto.AdvertPositionParamDto;
import com.miguan.xuanyuan.dto.common.AbTestAdvParamsDto;
import com.miguan.xuanyuan.vo.sdk.AdPositionVo;
import com.miguan.xuanyuan.vo.sdk.ConfigureInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 广告代码位service
 **/
public interface AdCodeService {

    /**
     * 获取广告代码位列表
     * @param queueVo AB测试vo
     * @param paramDto 接口参数
     * @return
     */
    AdDataDto adCodeInfoList(AbTestAdvParamsDto queueVo, AdvertCodeParamDto paramDto);

    /**
     * 统计多维度代码位ecpm
     * @param adIds  代码位列表
     * @param params 参数
     * @return
     */
    Map<String, Double> countMultiEcpm(List<String> adIds, Map<String, Object> params);

    AdPositionVo findPositionCustomRule(AbTestAdvParamsDto queueVo, AdvertPositionParamDto paramDto);

    void sortAutoMulti(List<AdCodeDto> advertVos, Map<String, Object> params);
    void setAdvertCodeDelayMillis(List<AdCodeDto> list);

    ConfigureInfoVo configureInfo(String appKey, String secretKey) throws Exception;
}
