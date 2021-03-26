package com.xiyou.speedvideo.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiyou.speedvideo.entity.FirstVideos;
import com.xiyou.speedvideo.entity.FirstVideosMca;
import com.xiyou.speedvideo.entity.FirstVideosMcaResult;
import com.xiyou.speedvideo.entity.LabelUpLoadLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 4:09 下午
 */
public interface FirstVideosMapper extends BaseMapper<FirstVideos>{

    @DS("xy-db")
    List<FirstVideos> getDownloadList(Map<String, Object> paramMap);

    @DS("bd_video")
    List<String> getMCAExistList();

    @DS("bd_video")
    int batchInsert2MCA(@Param("infoList") List<FirstVideos> infoList);

    @DS("bd_video")
    int updateMCAByVideoId(FirstVideosMca firstVideosMca);

    @DS("bd_video")
    FirstVideosMca selectMCAByVideoId(FirstVideosMca firstVideosMca);

    @DS("bd_video")
    int updateMCAResultBySource(FirstVideosMcaResult firstVideosMcaResult);

    @DS("bd_video")
    List<FirstVideosMcaResult> getMCAResult(Map<String, Object> paramMap);

    /**
     * 查询出需要解析的视频id和视频url
     * @param paramMap
     * @return
     */
    @DS("xy-db")
    List<FirstVideos> findVideoInfo(Map<String, Object> paramMap);

    /**
     * 根据视频id和视频url查询出已经解析的视频信息
     * @param paramMap
     * @return
     */
    @DS("bd_video")
    List<FirstVideosMcaResult> findVideoMcaResult(Map<String, Object> paramMap);

    @DS("bd_video")
    List<FirstVideosMcaResult> findVideoMcaHisResult(Map<String, Object> paramMap);

    /**
     * 根据白山云地址 查询出在百度云解析的视频id
     * @param bsyUrl 白山云地址
     * @return
     */
    @DS("bd_video")
    Integer getMcaVideoId(@Param("bsyUrl") String bsyUrl);

    /**
     * 保存中标签上报接口日志
     * @param labelUpLoadLog
     */
    @DS("bd_video")
    void insertLabelUploadLog(LabelUpLoadLog labelUpLoadLog);
}
