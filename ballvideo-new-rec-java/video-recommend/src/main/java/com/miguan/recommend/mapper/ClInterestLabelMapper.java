package com.miguan.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户兴趣标签选择表Mapper
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClInterestLabelMapper extends BaseMapper {

    String getCatIdsOfUserChoose(@Param("uuid") String uuid);
}