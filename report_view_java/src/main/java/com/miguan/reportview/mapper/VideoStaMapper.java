package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.VideoSta;
import com.miguan.reportview.vo.VideoStaVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 统计视频进文量和下线量(video_sta)数据Mapper
 *
 * @author zhongli
 * @since 2020-08-07 20:35:51
 * @description
 */
@Mapper
public interface VideoStaMapper extends BaseMapper<VideoSta> {

    List<VideoStaVo> getData(Map<String, Object> params);
}
