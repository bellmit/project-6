package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCatPoolMapper extends BaseMapper {

    @DS("ck-dw")
    public Long countDistinictIdWhereTopThreeInCatids(@Param("appPackage") String appPackage, @Param("catids") List<Integer> catids,
                                                      @Param("channels") List<String> channels);

    @DS("ck-dw")
    public List<String> findDistinictIdWhereTopThreeInCatids(@Param("appPackage") String appPackage, @Param("catids") List<Integer> catids,
                                                             @Param("channels") List<String> channels,
                                                             @Param("skipNum") Integer skipNum, @Param("size") Integer size);

    @DS("ck-dw")
    public Long initManualPushDistinct(@Param("dt") Integer dt, @Param("businessId") String businessId,
                                       @Param("receiveTime") String receiveTime, @Param("catids") List<Integer> catids);
}
