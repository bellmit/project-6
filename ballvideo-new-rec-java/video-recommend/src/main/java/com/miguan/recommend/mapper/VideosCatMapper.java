package com.miguan.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视频分类表Mapper
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface VideosCatMapper extends BaseMapper {

    List<String> getCatIdsByStateAndType(@Param("state") Integer state, @Param("type") String type);
}