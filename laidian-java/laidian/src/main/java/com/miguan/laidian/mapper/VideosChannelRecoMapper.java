package com.miguan.laidian.mapper;

import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.vo.VideosChanelRecoVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.cache.annotation.Cacheable;

/**
 * 视频源列表Mapper
 *
 * @author xy.chen
 * @date 2020-07-23
 **/
public interface VideosChannelRecoMapper {
    /**
     * 根据渠道获取渠道推荐内容
     *
     * @param channelId
     * @return
     */
    @Cacheable(value = CacheConstant.QUERY_VIDEOS_CHANNEL_RECO_INFO, unless = "#result == null")
    VideosChanelRecoVo queryVideosChannelRecoInfo(@Param("channelId") String channelId);
}
