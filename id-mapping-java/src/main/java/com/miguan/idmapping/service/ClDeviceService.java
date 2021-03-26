package com.miguan.idmapping.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.idmapping.entity.ClDevice;

/**
 * 用户表服务接口
 *
 * @author zhongli
 * @since 2020-09-01 11:40:54
 * @description 
 */
public interface ClDeviceService extends IService<ClDevice> {
    ClDevice findDevice(String deviceId, String appPackage);

    void updateDistinctId(String distinctId, String deviceId, String appPackage);
}
