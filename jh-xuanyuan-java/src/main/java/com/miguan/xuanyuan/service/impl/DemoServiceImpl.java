package com.miguan.xuanyuan.service.impl;

import com.miguan.xuanyuan.mapper.DemoMapper;
import com.miguan.xuanyuan.service.DemoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/1/14 15:17
 **/
@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    private DemoMapper demoMapper;

    public int countDemo() {
        return demoMapper.countDemo();
    }
}
