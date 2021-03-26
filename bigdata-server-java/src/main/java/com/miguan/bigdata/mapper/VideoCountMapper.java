package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.vo.VideoCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 监测接口mapper
 * @Author zhangbinglin
 * @Date 2020/11/6 9:01
 **/
public interface VideoCountMapper {

    /**
     * 统计单日分类有效播放率
     *
     * @param dt 统计日期 '20201126'
     * @return
     */
    @DS("ck-dw")
    public List<VideoCountVo> countCatVplayRate(@Param("dt") Integer dt, @Param("limit") Integer limit);

    /**
     * 统计单日某个分类下的视频有效播放率
     *
     * @param dt
     * @param catid
     * @return
     */
    @DS("ck-dw")
    public List<VideoCountVo> countVideoVplayRate(@Param("dt") Integer dt, @Param("catid") Integer catid, @Param("excludeVideos") List<Integer> excludeVideos, @Param("limit") Integer limit);
}
