package com.miguan.idmapping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.idmapping.entity.XyBuryingPointUser;
import com.miguan.idmapping.mapper.XyBuryingPointUserMapper;
import com.miguan.idmapping.service.IXyBuryingPointUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Service
public class XyBuryingPointUserServiceImpl extends ServiceImpl<XyBuryingPointUserMapper, XyBuryingPointUser> implements IXyBuryingPointUserService {

    @Override
    public XyBuryingPointUser findDevice(String deviceId, String appPackage) {
        LambdaQueryWrapper<XyBuryingPointUser> wrapper = Wrappers.lambdaQuery(XyBuryingPointUser.class).eq(XyBuryingPointUser::getDeviceId, deviceId)
                .eq(XyBuryingPointUser::getAppPackage, appPackage);
        return this.getOne(wrapper);
    }
}
