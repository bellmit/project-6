package com.miguan.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DwVideoActionMapper extends BaseMapper {

    /**
     * 根据用户行为统计指定分类的相似分类
     *
     * @param date  日期
     * @param catid 指定分类
     * @return
     */
    public List<Map<String, Object>> findSimilarCatid(@Param("date") String date, @Param("catid") String catid);
}
