package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.entity.QuHeadlinesCustomer;
import com.miguan.ballvideo.vo.CheckQuTouTiaoVo;

public interface QuHeadlinesCustomerMapper {

    int insertSelective(QuHeadlinesCustomer record);
    //查询安卓id和Imei
    QuHeadlinesCustomer selectByImeiAndAndroidid(CheckQuTouTiaoVo record);
}