package com.miguan.ballvideo.service.recommend;

import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.entity.recommend.UserFeature;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.FeatureService;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Resource
@Service
public class FeatureServiceImpl implements FeatureService, Runnable {

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;

    private UserFeature userFeature;
    private FirstVideos161Vo videoVo;

    @Override
    public void saveFeatureToRedis(UserFeature userFeature, FirstVideos161Vo videoVo) {
        this.userFeature = userFeature;
        this.videoVo = videoVo;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // 获取用户离线计算的活跃天数、分类兴趣度
        int activeDay = userFeature.getActiveDay();
        String uuid = userFeature.getPublicInfo().getUuid();
        List<String> catPoolList = userFeature.getCatPoolList();
        Map<String, Double> catFav = userFeature.getCatFav();
        try (Jedis con = jedisPool.getResource()) {
            // 获取用户标签池
            videoVo.getFirstVideosVos().stream().forEach(r -> {
                if (StringUtil.equals(r.getType(), "video")) {
                    String catId = r.getVideo().getCatId().toString();
                    double off_catFav = MapUtils.getDoubleValue(catFav, catId);
                    double real_catFav = this.calculateRealTimeCatFav(catPoolList, catId);
                    String key = String.format(RedisKeyConstant.video_snapshoot, uuid, r.getVideo().getId());
                    con.setex(key, 3600, activeDay + "," + off_catFav + "," + real_catFav);
                }
            });
        }
    }

    private double calculateRealTimeCatFav(List<String> catPoolList, String catId) {
        if (CollectionUtils.isEmpty(catPoolList)) {
            return 0.0D;
        }
        int index = catPoolList.indexOf(catId);
        return index == -1 ? 0:new BigDecimal(0.9).pow(index).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
