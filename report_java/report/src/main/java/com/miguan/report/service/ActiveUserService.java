package com.miguan.report.service;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.dto.PairLineDto;

import java.util.List;

/**
 * @Description 活跃用户service
 * @Author zhangbinglin
 * @Date 2020/6/20 14:09
 **/
public interface ActiveUserService {

    /**
     * 统计活跃用户比对报表数据(双Y轴折线图使用)
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @return
     */
    PairLineDto countActiveUserLineChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                               Integer isContrast, String appId, Integer rightStatItem);

    /**
     * 统计活跃用户比对报表数据
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @return
     */
    List<LineChartDto> countActiveUserChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                            Integer isContrast, String appId, Integer rightStatItem);

    /**
     * 统计活跃用户比对报表数据
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param detailType 等于1，则表示是明细表格接口进来
     * @return
     */
    List<LineChartDto> countActiveUserChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                            Integer isContrast, String appId, Integer rightStatItem, Integer detailType);

}
