package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User>{

    public User getUserInfo(String username);

    List<User> listValidUser(Map<String, Object> param);
}
