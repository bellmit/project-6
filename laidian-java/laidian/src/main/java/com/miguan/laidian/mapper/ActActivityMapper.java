package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.entity.ActActivityExample;
import java.util.List;
import java.util.Map;

import com.miguan.laidian.redis.util.CacheConstant;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

public interface ActActivityMapper {
    long countByExample(ActActivityExample example);

    int deleteByExample(ActActivityExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActActivity record);

    int insertSelective(ActActivity record);

    List<ActActivity> selectByExample(ActActivityExample example);

    ActActivity selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActActivity record, @Param("example") ActActivityExample example);

    int updateByExample(@Param("record") ActActivity record, @Param("example") ActActivityExample example);

    int updateByPrimaryKeySelective(ActActivity record);

    int updateByPrimaryKey(ActActivity record);

    @Cacheable(value = CacheConstant.FIND_ACT_ACTIVITY, unless = "#result == null")
    List<ActActivity> findActActivity(Map<String,Object> params);
}