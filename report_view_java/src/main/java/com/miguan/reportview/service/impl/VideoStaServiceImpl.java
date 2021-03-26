package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.reportview.entity.VideoSta;
import com.miguan.reportview.mapper.VideoStaMapper;
import com.miguan.reportview.service.IVideoStaService;
import com.miguan.reportview.vo.ParamsBuilder;
import com.miguan.reportview.vo.VideoStaVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统计视频进文量和下线量服务接口实现
 *
 * @author zhongli
 * @since 2020-08-07 20:35:51
 * @description
 */
@RequiredArgsConstructor
@Service
@DS("report-db")
public class VideoStaServiceImpl extends ServiceImpl<VideoStaMapper, VideoSta> implements IVideoStaService {
    private final VideoStaMapper videoStaMapper;


    @Override
    public List<VideoStaVo> getData(String startDate, String endDate, boolean isGroupByCatId) {
        Map<String, Object> params = ParamsBuilder.builder(3)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("groups", isGroupByCatId ? "1" : null)
                .get();
        return videoStaMapper.getData(params);
    }
}