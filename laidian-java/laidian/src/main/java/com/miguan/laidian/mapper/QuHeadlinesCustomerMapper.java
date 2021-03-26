package com.miguan.laidian.mapper;



import com.miguan.laidian.entity.QuHeadlinesCustomer;
import com.miguan.laidian.vo.CheckQuTouTiaoVo;

import java.util.List;

public interface QuHeadlinesCustomerMapper {

    int insertSelective(QuHeadlinesCustomer record);

    QuHeadlinesCustomer selectByImeiAndAndroidid(CheckQuTouTiaoVo record);
}