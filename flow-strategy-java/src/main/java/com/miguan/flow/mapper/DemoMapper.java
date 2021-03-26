package com.miguan.flow.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.flow.dto.DemoDto;

import java.util.List;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/12/7 14:13
 **/
public interface DemoMapper {
    @DS("adv")
    List<DemoDto> listDeom();
}

