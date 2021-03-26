package com.miguan.recommend.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCatPoolMapper extends BaseMapper {

    @DS("clickhouse-dw")
    public Long countDistinictIdWhereTopThreeInCatids(@Param("appPackage") String appPackage, @Param("catids") List<Integer> catids,
                                                      @Param("channels") List<String> channels);

    @DS("clickhouse-dw")
    public List<String> findDistinictIdWhereTopThreeInCatids(@Param("appPackage") String appPackage, @Param("catids") List<Integer> catids,
                                                             @Param("channels") List<String> channels,
                                                             @Param("skipNum") Integer skipNum, @Param("size") Integer size);
}
