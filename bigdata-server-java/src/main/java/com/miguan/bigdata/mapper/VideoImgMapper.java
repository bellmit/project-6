package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.vo.FirstVideosVo;
import com.miguan.bigdata.vo.RepeatVideoLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 视频标题和背景图片相似度查询mapper
 */
public interface VideoImgMapper {

    /**
     * 分页查询视频信息
     * @return
     */
    @DS("xy-db")
    List<FirstVideosVo> fingVideoByPage(Map<String, Object> params);

    @DS("xy-db")
    void batchInsertRepeatVideoLog(@Param("list") List<RepeatVideoLogVo> list);
}
