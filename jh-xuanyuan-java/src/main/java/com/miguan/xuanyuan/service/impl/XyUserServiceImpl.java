package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.entity.Identity;
import com.miguan.xuanyuan.entity.XyUser;
import com.miguan.xuanyuan.mapper.XyUserMapper;
import com.miguan.xuanyuan.service.IdentityService;
import com.miguan.xuanyuan.service.XyUserService;
import com.miguan.xuanyuan.service.common.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-04
 */
@Service
public class XyUserServiceImpl extends ServiceImpl<XyUserMapper, XyUser> implements XyUserService {

    @Resource
    XyUserMapper mapper;

    @Resource
    IdentityService identityService;


    public XyUser getUserByUsername(String username) {
        return mapper.getUserByUsername(username);
    }

    public XyUser getUserByPhone(String phone) {
        return mapper.getUserByPhone(phone);
    }

    @Transactional
    public void saveUser(XyUser xyUser) {
        this.save(xyUser);
//        Identity identity = new Identity();
//        identity.setUserId(xyUser.getId());
//        identity.setType(xyUser.getType());
//        identityService.saveIdentity(identity);
    }




}
