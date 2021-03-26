package com.miguan.xuanyuan.service.third;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Description 第三方Service
 * @Author zhangbinglin
 **/
public interface ThirdPlatApiService {

    /**
     * 调用第三方api接口，导入第三方广告数据
     * @param date 开始日期，格式：yyyy-MM-dd
     * @param isCover 如果数据已经导入，是否重新调用第三方api接口，并覆盖数据。true：覆盖
     */
    void syncThirdPlatAdvData(String date, boolean isCover);


    /**
     * 设置redis缓存，用来确定第三方平台的广告数据是否已经成功的自动导入了
     * @param date
     * @param appId
     * @param secret
     * @return
     */
    void setSuccessTag(String date, String appId, String secret);

    /**
     * 按天保存广告配置信息
     * @param date 日期，格式：yyyy-MM-dd
     */
    void syncAdConfigLog(String date);
}
