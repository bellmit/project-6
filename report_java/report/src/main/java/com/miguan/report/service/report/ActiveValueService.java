package com.miguan.report.service.report;

import com.miguan.report.vo.ActiveValueVo;

import java.util.List;

/**日活均价值服务接口
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public interface ActiveValueService {

    /**
     * 按应用或平台分组统计
     * @param startDate 查询此【起始日期】的数据
     * @param endDate   查询此【结束日期】的数据
     * @param adSpace   广告位
     * @param showDateType 1=按天统计 2=按周统计 3=按月统计
     * @param groupType 1=按应用统计 2=按平台统计
     * @param appType 1=视频 2=来电
     * @return
     */
    List<ActiveValueVo> loadStaData(String startDate, String endDate, String adSpace, int showDateType, int groupType, int appType);
}
