package com.miguan.idmapping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.idmapping.entity.XyBuryingPointUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
public interface IXyBuryingPointUserService extends IService<XyBuryingPointUser> {
    XyBuryingPointUser findDevice(String deviceId, String appPackage);
}
