package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.mapper.DemoMapper;
import com.miguan.xuanyuan.mapper.UserMapper;
import com.miguan.xuanyuan.service.DemoService;
import com.miguan.xuanyuan.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询生效中的用户列表
     * @return
     */
    public List<User> listUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);  //生效状态
        queryWrapper.eq("user_type", 1);  //用户类型：媒体账号
        queryWrapper.orderByDesc("created_at");
        List<User> list = userMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 查询身份已认证或待审核的用户
     * @return
     */
    public List<User> listValidUser(int plat) {
        Map<String,Object> param = Maps.newHashMap();
        param.put("plat",plat);
        param.put("status",1);
        List<User> list = userMapper.listValidUser(param);
        return list;
    }

    /**
     * 保存用户信息
     * @param user
     */
    public void saveUser(User user) {
        if(user.getId() == null) {
            //新增操作
            userMapper.insert(user);
        } else {
            //修改操作
            userMapper.updateById(user);
        }
    }

    /**
     * 判断用户账号是否存在
     * @param username 账号名称
     * @param id 用户id
     * @return
     */
    public boolean isExistUserName(Integer id, String username) {
        if(StringUtils.isBlank(username)) {
            return true;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);  //账号名称
        if(id != null) {
            queryWrapper.ne("id", id);
        }
        List<User> list =userMapper.selectList(queryWrapper);
        return list.size() > 0;
    }

    /**
     * 根据用户账号查询用户信息
     * @param username 用户账号
     * @return
     */
    public User queryUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 判断用户账号是否存在
     * @param phone 注册手机号
     * @param id 用户id
     * @return
     */
    public boolean isExistUserPhone(Integer id, String phone) {
        if(StringUtils.isBlank(phone)) {
            return true;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        if(id != null) {
            queryWrapper.ne("id", id);
        }
        List<User> list =userMapper.selectList(queryWrapper);
        return list.size() > 0;
    }
}
