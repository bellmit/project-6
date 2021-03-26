package com.miguan.ballvideo.service;


import com.miguan.ballvideo.vo.CheckQuTouTiaoVo;
import com.miguan.ballvideo.vo.SaveQuTouTiaoVo;

import java.util.Map;

public interface QuTouTiaoService {

    Map<String, Object> insertSelective(SaveQuTouTiaoVo record);

    //查询安卓id和Imei
    Map<String, Object> selectByImeiAndAndroidid(CheckQuTouTiaoVo record);

}
