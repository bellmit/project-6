package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.Video;
import com.miguan.laidian.vo.VideoLabelVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VideoLabelMapper {

    /**
     * 查询热门搜索标签
     *
     * @return
     */
   List<VideoLabelVo> topSearchLabel();

    /**
     * 根据标签名称查询标签ID
     *
     * @param likeLabelName
     * @return
     */
   List<Long> queryLabelIdsByName(@Param("likeLabelName") String likeLabelName);

    /**
     * 通过标签信息查询视频列表
     *
     * @param params
     * @return
     */
   List<Video> findLabelVideosList(Map<String,Object> params);
}
