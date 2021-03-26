package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.entity.XyPlanShare;

/**
 * <p>
 * 广告计划分享表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
public interface XyPlanShareService extends IService<XyPlanShare> {

    String generateShareCode(Long planId, Long userId) throws Exception;
}
