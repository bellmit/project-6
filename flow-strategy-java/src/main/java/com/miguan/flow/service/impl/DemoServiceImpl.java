package com.miguan.flow.service.impl;

import com.miguan.flow.dto.DemoDto;
import com.miguan.flow.mapper.DemoMapper;
import com.miguan.flow.service.DemoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/12/7 11:55
 **/
@Service
public class DemoServiceImpl implements DemoService{

    @Resource
    private DemoMapper demoMapper;

    public List<DemoDto> listDeom() {
        return demoMapper.listDeom();
    }
}
