package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.PushContent;
import com.miguan.laidian.redis.util.CacheConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

@Mapper
public interface PushContentMapper {


    @Cacheable(value = CacheConstant.FIND_PUSH_CONTENT_LIST, unless = "#result == null")
    @Select("SELECT * from push_content where content_type = #{contentType}")
    List<PushContent> findPushContentList(@Param("contentType") Integer contentType);
}