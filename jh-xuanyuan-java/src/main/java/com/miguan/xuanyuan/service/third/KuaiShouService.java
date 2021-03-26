package com.miguan.xuanyuan.service.third;

import com.alibaba.fastjson.JSONArray;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;

import java.util.List;

/**
 * @Description 第三方快手Service
 * @Author zhangbinglin
 * @Date 2020/7/13 11:33
 **/
public interface KuaiShouService {

    /**
     * 获取分成数据（日级别）
     * @param date
     * @return
     */
    JSONArray getDailyShare(String date, String ak, String sk);


    /**
     * 获取快手广告接口统计数据
     * @param date 格式yyy-MM-dd
     * @param ak
     * @param sk
     * @return
     */
    List<ThirdPlatDataVo> getDataList(String date, String ak, String sk);
}
