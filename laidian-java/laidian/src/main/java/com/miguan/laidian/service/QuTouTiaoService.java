package com.miguan.laidian.service;

import com.miguan.laidian.entity.QuHeadlinesCustomer;
import com.miguan.laidian.vo.CheckQuTouTiaoVo;
import com.miguan.laidian.vo.SaveQuTouTiaoVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface QuTouTiaoService {

    Map<String, Object> insertSelective(SaveQuTouTiaoVo record);

    Map<String, Object> selectByImeiAndAndroidid(CheckQuTouTiaoVo record);

}
