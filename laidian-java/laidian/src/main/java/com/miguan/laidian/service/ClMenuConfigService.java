package com.miguan.laidian.service;

import com.miguan.laidian.vo.ClMenuConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 菜单栏配置表Service
 *
 * @author xy.chen
 * @date 2019-08-23
 **/

public interface ClMenuConfigService {

    List<ClMenuConfigVo> findClMenuConfigInfo(Map<String, Object> params);
}