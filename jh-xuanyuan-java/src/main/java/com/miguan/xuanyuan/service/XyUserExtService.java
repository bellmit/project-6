package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.entity.XyUserExt;

/**
 * <p>
 * 用户扩展表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
public interface XyUserExtService extends IService<XyUserExt> {

    void setShareSwitch(long userId, int shareSwitch) throws ServiceException;

    XyUserExt getUserExtByUserId(Long userId);

    Integer getUserShareSwitch(Long userId);
}
