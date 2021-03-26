package com.miguan.reportview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.reportview.entity.AdAdvertPosition;
import com.miguan.reportview.mapper.AdAdvertPositionMapper;
import com.miguan.reportview.service.IAdAdvertPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 广告位置表（广告相关表统一ad_开头）服务接口实现
 *
 * @author zhongli
 * @since 2020-08-07 16:25:24
 * @description
 */
@RequiredArgsConstructor
@Service
public class AdAdvertPositionServiceImpl extends ServiceImpl<AdAdvertPositionMapper, AdAdvertPosition> implements IAdAdvertPositionService {
    private final AdAdvertPositionMapper adAdvertPositionMapper;


}