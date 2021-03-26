package com.miguan.recommend.service.xy.impl;

import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.xy.AblumService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class AblumServiceImpl implements AblumService {

    @Resource(name = "xyRedisDB2Service")
    private RedisService xyRedisDB2Service;

    /**
     * 获取视频专辑ID
     *
     * @param videoId
     * @return
     */
    @Override
    public int getVideoAblumId(String videoId) {
        String ablumId = xyRedisDB2Service.hget(XyConstants.video_ablum_info, videoId);
        if (isEmpty(ablumId)) {
            return 0;
        }
        return Integer.parseInt(ablumId);
    }
}
