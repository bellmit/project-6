package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.result.UserDetailInfoVo;

import java.util.List;

public interface UserMapper {

    /**
     * 查询用户列表
     * @return
     */
    List<UserDetailInfoVo> getUserList();

}
