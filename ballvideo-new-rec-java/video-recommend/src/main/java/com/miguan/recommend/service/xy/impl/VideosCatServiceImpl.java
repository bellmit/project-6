package com.miguan.recommend.service.xy.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.mapper.VideosCatMapper;
import com.miguan.recommend.service.xy.VideosCatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首页视频分类表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@DS("xy-db")
@Service("videosCatService")
public class VideosCatServiceImpl implements VideosCatService {

    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;
    @Resource
    private VideosCatMapper videosCatMapper;

    @Override
    public List<String> getAllCatIds(String type) {
        return videosCatMapper.getCatIdsByStateAndType(null, type);
    }

    @Override
    public List<String> getCatIdsByStateAndType(Integer state, String type) {
        String key = RedisRecommendConstants.all_catids + state + "_" + type;
        try (Jedis con = jedisPool.getResource()) {
            String redisValue = con.get(key);
            if (StringUtils.isNotBlank(redisValue)) {
                return new ArrayList<>(Arrays.asList(redisValue.split(",")));
            }

            List<String> catIds_db = videosCatMapper.getCatIdsByStateAndType(state, type);
            if (!CollectionUtils.isEmpty(catIds_db)) {
                con.setex(key, ExistConstants.four_hour_seconds, String.join(",", catIds_db));
            }
            return catIds_db;
        }
    }

}