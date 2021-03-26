package com.miguan.xuanyuan.service.third;

import com.alibaba.fastjson.JSONArray;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;

import java.util.List;

/**
 * @Description 第三方广点通Service
 * @Author zhangbinglin
 * @Date 2020/7/13 11:33
 **/
public interface GuanDianTongService {
    /**
     * 查询广点通报表数据(包含点击数，展现数，营收等数据)
     * @param startDate 开始日期，yyyyMMdd，时间跨度不超过2天
     * @param endDate 截至日期，yyyyMMdd，时间跨度不超过2天
     * @return
     */
    JSONArray getReportDatas(String startDate, String endDate, String memberid, String secret);

    /**
     * 获取快手广告接口统计数据
     * @param date 格式yyy-MM-dd
     * @param memberid
     * @param secret
     * @return
     */
    List<ThirdPlatDataVo> getDataList(String date, String memberid, String secret);
}
