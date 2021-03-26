package com.miguan.ballvideo.service.recommend;

import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.service.FeatureParamService;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FeatureParamServiceImpl implements FeatureParamService {

    @Resource(name="recDB9Pool")
    private JedisPool recDB9Pool;

    @Override
    public void snapshotToRedis(VideoParamsDto videoParamsDto, List<Videos161Vo> videoList) {
        
    }
}
