package com.miguan.recommend.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.recommend.vo.RecVideosVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FirstVideosMapper {

    @DS("xy-db")
    public Integer count();

    @DS("xy-db")
    public RecVideosVo findById(@Param("videoId") Integer videoIds);

    @DS("xy-db")
    public List<RecVideosVo> findByIds(@Param("videoIds") List<Integer> videoIds);

    @DS("xy-db")
    public List<RecVideosVo> findIncentiveVideo();

    @DS("xy-db")
    public List<RecVideosVo> findInPage(@Param("skip") Integer skip, @Param("size") Integer size);
}
