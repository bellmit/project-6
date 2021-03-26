package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.UserCatPoolService;
import com.miguan.ballvideo.service.redis.RedisRecDB9Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserCatPoolServiceImpl implements UserCatPoolService {

    @Resource
    private RedisRecDB9Service recDB9Service;

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Override
    public boolean addRedisVideosCatInfo(Map<String, Object> params) {
        String deviceId = MapUtils.getString(params, "deviceId");
        String id = MapUtils.getString(params, "id");
        if (!StringUtils.isEmpty(deviceId) && !StringUtils.isEmpty(id)) {
            Long videoId = Long.valueOf(id);
            String catId = firstVideosMapper.findCatIdByVideoId(videoId);
            log.warn("更新用户感兴趣的分类池:uuid>>{}, catId>>{}", deviceId, catId);
            // 0和1001分类不更新到用户的兴趣池
            if(StringUtils.isEmpty(catId) || "0".equals(catId) || "1001".equals(catId)){
                return false;
            }
            // 获取用户感兴趣的分类池
            String key = RedisKeyConstant.USER_LIKE_CAT_POOL;
            String catPool = recDB9Service.hget(key, deviceId);
            if(log.isDebugEnabled()){
                log.debug("用户原有感兴趣的分类池>>{}", catPool);
            }
            if (StringUtils.isEmpty(catPool)) {
                catPool = catId;
            } else {
                String[] catArray = catPool.split(",");
                List<String> catList = new ArrayList<>();
                catList.addAll(CollectionUtils.arrayToList(catArray));
                // 移除当前分类ID，并添加到最后
                catList.remove(catId);
                List<String> newCatList = new ArrayList<>();
                newCatList.add(catId);
                newCatList.addAll(catList);
                // 保留最近的20个
                int catPoolSize = 20;
                if (newCatList.size() > catPoolSize){
                    newCatList.remove(catPoolSize);
                }
                catPool = StringUtils.collectionToDelimitedString(newCatList, ",");
            }
            // 保存用户感兴趣的分类池
            log.warn("用户最新感兴趣的分类池:uuid>>{}, >>{}", deviceId, catPool);
            recDB9Service.hset(key, deviceId, catPool);
        }
        return false;
    }
}
