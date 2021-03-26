package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.XyUser;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-04
 */
public interface XyUserMapper extends BaseMapper<XyUser> {

    XyUser getUserByUsername(String username);

    XyUser getUserByPhone(String phone);

}
