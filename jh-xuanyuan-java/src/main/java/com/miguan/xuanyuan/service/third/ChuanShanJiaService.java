package com.miguan.xuanyuan.service.third;

import com.alibaba.fastjson.JSONArray;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;

import java.util.List;

/**
 * @Description 第三方穿山甲Service
 * @Author zhangbinglin
 * @Date 2020/7/13 11:33
 **/
public interface ChuanShanJiaService {

    /**
     * 请求穿山甲接口
     * @param startDate 开始日期，格式：yyyy-MM-dd
     * @param endDate 结束日期, 格式：yyyy-MM-dd
     * @return
     */
    JSONArray getReportDatas(String startDate, String endDate, String username, String appid, String secret);

    /**
     * 获取穿山甲接口广告接口数据
     * @param date 开始日期，格式：yyyy-MM-dd
     * @param appid appid
     * @param secret secret
     * @return
     */
    List<ThirdPlatDataVo> getDataList(String date, String username, String appid, String secret);
}
