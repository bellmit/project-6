package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.entity.XyUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-04
 */
public interface XyUserService extends IService<XyUser> {

    XyUser getUserByUsername(String username);

    XyUser getUserByPhone(String phone);

    void saveUser(XyUser xyUser);
}
