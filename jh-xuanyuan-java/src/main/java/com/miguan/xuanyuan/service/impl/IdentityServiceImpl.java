package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cgcg.base.core.exception.CommonException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.dto.IdentityBackDto;
import com.miguan.xuanyuan.dto.IdentityListBackDto;
import com.miguan.xuanyuan.entity.Identity;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.mapper.IdentityMapper;
import com.miguan.xuanyuan.mapper.UserMapper;
import com.miguan.xuanyuan.service.IdentityService;
import com.miguan.xuanyuan.service.UserService;
import com.miguan.xuanyuan.service.XyPlatAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 账号认证表 服务实现类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-27
 */
@Service
public class IdentityServiceImpl implements IdentityService {

    @Resource
    private IdentityMapper identityMapper;
    @Resource
    private UserService userService;
    @Resource
    private XyPlatAccountService xyPlatAccountService;

    /**
     * 分页查询媒体账号列表
     * @param phone  用户注册电话
     * @param name  企业/个人名称
     * @param status  状态
     * @param pageNum  分页页码
     * @param pageSize 分页每页记录数
     * @return
     */
    public PageInfo<IdentityListBackDto> pageIdentityList(String phone, String name, Integer status, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("phone", phone);
        params.put("name", name);
        params.put("status", status);
        PageHelper.startPage(pageNum, pageSize);
        Page<IdentityListBackDto> pageResult = identityMapper.pageIdentityList(params);
        return new PageInfo(pageResult);
    }

    /**
     * 保存媒体账号信息（根据id进行新增或修改操作）
     * @param identity
     */
    public void saveIdentity(Identity identity) {
        if(identity.getId() == null) {
            //新增操作
            identityMapper.insert(identity);
        } else {
            //修改操作
            identityMapper.updateById(identity);
        }
    }

    /**
     * 后台保存媒体账号信息
     * @param identityBackDto
     */
    public void saveBackIdentity(IdentityBackDto identityBackDto) {
        //保存xy_user表信息
        boolean isNewUser = (identityBackDto.getId() == null); //是否新媒体账号
        User user = new User();
        user.setUsername(identityBackDto.getUsername());
        user.setPhone(identityBackDto.getPhone());
        user.setType(identityBackDto.getType());
        if(isNewUser) {
            BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
            String password = encode.encode(XyConstant.INIT_PASSWORD);
            user.setPassword(password);  //设置初始密码
        } else {
            user.setId(identityBackDto.getId());
        }
        if(userService.isExistUserName(user.getId(), user.getUsername())) {
            throw new CommonException("媒体账号已存在，请重新输入!");
        }
        if(userService.isExistUserPhone(user.getId(), user.getPhone())) {
            throw new CommonException("注册手机号已存在，请重新输入!");
        }
        userService.saveUser(user);
        if(isNewUser) {
            //每个账号注册后，初始化【穿山甲】【广点通】【快手】三个平台，仅做账号管理功能
            xyPlatAccountService.insertDefaultPlatAccount(user.getId());
        }


        //保存xy_identity表信息
        Identity identity = new Identity();
        BeanUtils.copyProperties(identityBackDto, identity);
        identity.setId(null);
//        identity.setId(identityBackDto.getIdentityId()); //前端传过来的数据，存在多次插入的情况
        Identity userIdentity = this.getIdentityByUserId(user.getId());
        if (userIdentity != null) {
            identity.setId(userIdentity.getId());
        }

        identity.setUserId(user.getId().intValue());
        this.saveIdentity(identity);
    }


    /**
     * 根据id查询媒体账号信息
     * @param id
     * @return
     */
    public Identity getIdentity(Integer id) {
        return identityMapper.selectById(id);
    }

    /**
     * 根据userId查询媒体信息
     * @param userId
     * @return
     */
    public Identity getIdentityByUserId(Integer userId) {
        QueryWrapper<Identity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.identityMapper.selectOne(queryWrapper);
    }

    /**
     * 获取后台媒体账号信息
     * @param id
     * @return
     */
    public IdentityBackDto getBackIdentity(Integer identityId, Integer id) {
        Identity identity = new Identity();
        if(identityId != null) {
            identity = this.getIdentity(identityId); ////查询出用户媒体账号信息
        }
        User user = userService.getById(id);    //查询出用户信息

        IdentityBackDto identityBackDto = new IdentityBackDto();
        BeanUtils.copyProperties(identity, identityBackDto);
        BeanUtils.copyProperties(user, identityBackDto);
        identityBackDto.setId(user.getId().intValue());
        identityBackDto.setIdentityId(identity.getId());
        identityBackDto.setStatus(identity.getStatus());
        return identityBackDto;
    }

    public Identity getIdentityByUserId(Long userId) {
        return this.identityMapper.getIdentityByUserId(userId);
    }
}
