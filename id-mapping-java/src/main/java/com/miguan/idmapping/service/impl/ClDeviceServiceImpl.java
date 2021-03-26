package com.miguan.idmapping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.idmapping.entity.ClDevice;
import com.miguan.idmapping.mapper.ClDeviceMapper;
import com.miguan.idmapping.service.ClDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户表服务接口实现
 *
 * @author zhongli
 * @since 2020-09-01 11:40:54
 * @description
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ClDeviceServiceImpl extends ServiceImpl<ClDeviceMapper, ClDevice> implements ClDeviceService {
    private final ClDeviceMapper clDeviceMapper;

    @Override
    public ClDevice findDevice(String deviceId, String appPackage) {
        LambdaQueryWrapper<ClDevice> wrapper = Wrappers.lambdaQuery(ClDevice.class)
                .eq(ClDevice::getDeviceId, deviceId)
                .isNotNull(ClDevice::getCreateTime)
                .orderByAsc(ClDevice::getCreateTime);
        List<ClDevice> list = clDeviceMapper.selectList(wrapper);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    public void updateDistinctId(String distinctId, String deviceId, String appPackage) {
        clDeviceMapper.updateDistinctId(distinctId, deviceId, appPackage);
    }
}