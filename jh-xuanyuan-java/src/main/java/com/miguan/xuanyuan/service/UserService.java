package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    User getUserById(Integer id);

    /**
     * 查询生效中的用户列表
     * @return
     */
    List<User> listUser();

    /**
     * 保存用户信息
     * @param user
     */
    void saveUser(User user);

    /**
     * 判断用户账号是否存在
     * @param username 账号名称
     * @param id 用户id
     * @return
     */
    boolean isExistUserName(Integer id, String username);

    /**
     * 判断用户手机号是否存在
     * @param phone 账号名称
     * @param id 用户id
     * @return
     */
    boolean isExistUserPhone(Integer id, String phone);

    List<User> listValidUser(int plat);

    /**
     * 根据用户账号查询用户信息
     * @param username 用户账号
     * @return
     */
    User queryUserByUsername(String username);
}
