package com.miguan.report.service.report;

import com.miguan.report.dto.SelectListDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description service
 * @Author zhangbinglin
 * @Date 2020/6/17 18:38
 **/
public interface SelectService {

    /**
     * 下拉列表-详情页
     * @param appType 类型：1=西柚视频,2=炫来电
     * @return
     */
    SelectListDto detailList(Integer appType);

    /**
     * 下拉选项-获取代码为ID列表
     * @param keyword 关键字
     * @param appClientId 应用客户端
     * @param platForm 平台
     * @param totalName 广告位置名称
     * @param appType 类型：1=西柚视频,2=炫来电
     * @return
     */
    List<String> getAdSpaceIdList(String keyword, String appClientId, String platForm, String totalName, Integer appType);
}
