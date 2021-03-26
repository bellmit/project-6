package com.miguan.report.service.report;

import com.miguan.report.dto.BannerQuotaDto;
import com.miguan.report.dto.DataDetailDto;
import com.miguan.report.dto.LineChartDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 数据明细service
 * @Author zhangbinglin
 * @Date 2020/6/20 14:09
 **/
public interface DataDetailService {

    /**
     * 查询数据明细列表（分页）
     *
     * @param pageNum     页码
     * @param pageSize    每页记录数
     * @param startDate   统计开始时间
     * @param endDate     统计结束时间
     * @param token       token
     * @param appType     app类型：1=西柚视频,2=炫来电
     * @param appClientId 应用客户端id
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param sortField        排序字段,格式:升序(字段id)，降序(字段id+空格+desc)
     * @return
     */
    DataDetailDto findDataDetailList(int pageNum, int pageSize, String startDate, String endDate, String token, Integer appType,
                                     String appClientId, String platForm, String totalName, String adId, String sortField);

    /**
     * 数据明细折线图
     *
     * @param startDate   开始时间
     * @param endDate     结束时间
     * @param appType     app类型：1=西柚视频,2=炫来电(默认值为1)
     * @param statItem    统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率
     * @param appClientId 应用id(下拉列表取appClientList)
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param timeType    时间类型：0-按小时，1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    List<LineChartDto> countLineDataDetailChart(String startDate, String endDate, Integer appType, String statItem, String appClientId,
                                                String platForm, String totalName, String adId, Integer timeType);

    /**
     * 按小时数据明细折线图
     *
     * @param date        日期，多个的时候逗号分隔
     * @param appType     app类型：1=西柚视频,2=炫来电(默认值为1)
     * @param statItem    统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率
     * @param appClientId 应用id(下拉列表取appClientList)
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @return
     */
    List<LineChartDto> countLineHourChart(String date, Integer appType, String statItem, String appClientId, String platForm, String totalName, String adId);

    /**
     * 查询数据明细列表（分页）
     *
     * @param startDate   统计开始时间
     * @param endDate     统计结束时间
     * @param token       token
     * @param appType     app类型：1=西柚视频,2=炫来电
     * @param appClientId 应用客户端id
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param sortField
     * @return
     */
    void export(HttpServletResponse response, String startDate, String endDate, String token, Integer appType,
                String appClientId, String platForm, String totalName, String adId, String sortField);

    /**
     * 保存用户自定义列
     * @param token
     * @param appType
     * @param ids
     */
    void saveHeadField(String token, Integer appType, String ids);

    /**
     * 如果没有选择代码位，则过滤掉代码后后面的字段
     * @param headDtoList
     */
    void filterAdFiled(List<BannerQuotaDto> headDtoList);
}
