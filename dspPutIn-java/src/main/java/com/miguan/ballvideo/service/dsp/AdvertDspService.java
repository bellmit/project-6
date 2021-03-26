package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.entity.dsp.AdvDspSqlInfo;
import com.miguan.ballvideo.entity.dsp.AdvZoneValExpVo;
import com.miguan.ballvideo.entity.dsp.AdvertDspInfo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * DSP广告投放Service
 * @author suhongju
 * @date 2020-08-21
 */
public interface AdvertDspService {
    /**
     * 查询广告信息
     * @param queueVo
     * @param param
     * @return
     */
    List<AdvertCodeVo> commonSearch(AbTestAdvParamsVo queueVo, Map<String, Object> param);

    /**
     * 判断有效性:appId,secretkey,poskey
     * @param param
     * @return
     */
    Map<String, Integer> judgeValidity(Map<String, Object> param);
    /**
     * 过滤投放平台没有的广告位，并通过竞价排序
     * @param advertCodeVoList
     * @return
     */
    void cpmAdvCode(List<AdvertCodeVo> advertCodeVoList);


    /**
     * 判断是否为98自投平台的代码位
     * @param code
     * @param appPackage
     * @param mobileType
     * @return
     */
    String judgeAdvPlat(String code, String appPackage, String mobileType);

    /**
     * 新的一天的话，重新统计日预算
     * @param list
     */
    void updateNewDayAccount(List<AdvDspSqlInfo> list);

    /**
     * 获取广告内容
     * @param appId
     * @param code
     * @param area
     * @param devTp
     * @param device
     * @param uuid
     * @param appVersion
     * @return
     */
    AdvertDspInfo getAdvInfo(String appId,String code,String area,String devTp,String device,String uuid,String appVersion);


    /**
     * 判断订单是否存在
     * @param appId
     * @param orderId
     * @param advertOrderEffectMongodb
     * @return
     */
    Boolean judgeOrderExsist(String appId, String orderId, String advertOrderEffectMongodb);

    /**
     * 上报订单扣费事件
     * @param planId
     */
    Boolean upOrderReport(String planId);

    /**
     * 保存订单并上报埋点
     * @param advZoneValExpVo
     * @param publicInfo
     * @param request
     * @param orderId
     * @param device
     * @param appId
     * @param advertOrderEffectMongodb
     */
    void saveOrderAndSendToMQ(AdvZoneValExpVo advZoneValExpVo, String publicInfo, HttpServletRequest request,
                              String orderId, String device, String appId, String advertOrderEffectMongodb);

    /**
     * 初始化配置参数
     */
    void initDspConfig();

    /**
     * 创建计划账户
     * @param planId 计划id
     * @param remainDayPrice 剩余日预算
     * @param remainGroupPrice 剩余组预算
     * @param remainTotalPrice 剩余总预算
     */
    void createdPlanAccount(Integer planId, double remainDayPrice, double remainGroupPrice, double remainTotalPrice);
}
