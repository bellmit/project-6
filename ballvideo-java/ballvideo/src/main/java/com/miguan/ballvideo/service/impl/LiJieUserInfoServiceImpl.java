package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.mapper.ClDeviceMapper;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.repositories.UserLabelGradeJpaRepository;
import com.miguan.ballvideo.repositories.UserLabelJpaRepository;
import com.miguan.ballvideo.service.LiJieUserInfoService;
import com.miguan.ballvideo.service.RedisDB8Service;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.ClUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("liJieUserInfoService")
public class LiJieUserInfoServiceImpl implements LiJieUserInfoService {

    @Resource
    private ClUserMapper clUserMapper;

    @Resource
    private ClDeviceMapper clDeviceMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisDB8Service redisDB8Service;

    @Resource
    private UserLabelJpaRepository userLabelJpaRepository;

    @Resource
    private UserLabelGradeJpaRepository userLabelGrade;

    @Resource(name = "idmappingMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteUserInfo() {
        clUserMapper.deleteDeviceId();
        clDeviceMapper.deleteDeviceId();
        userLabelJpaRepository.deleteUserInfo();
        userLabelGrade.deleteUserInfo();

        String key = "findUserBuryingPointIsNew::ballVideos:cacheAble:findUserBuryingPointIsNew:4f13322e9e48d4e0";
        redisService.del(key);
        String key1 = "ballVideos:userLabe:4f13322e9e48d4e0";
        redisService.del(key1);

        //根据key模糊查询删除
        Set<String> set = redisDB8Service.keys("ballVideos:showedByIds:4f13322e9e48d4e0:*");
        if (set != null) {
            for (String keyDelete : set) {
                redisDB8Service.del(keyDelete);
            }
        }
    }

    @Override
    public int deleteUserInfo(String deviceId, String type) {
        String appPackage = "com.mg.xyvideo";
        if (StringUtils.isNotEmpty(type)) {
            if ("2".equals(type)) {
                appPackage = "com.mg.ggvideo";
            } else if ("3".equals(type)) {
                appPackage = "com.mg.dqvideo";
            } else if ("4".equals(type)) {
                appPackage = "com.mg.mtvideo";
            } else if ("5".equals(type)) {
                appPackage = "com.mg.quickvideo";
            }
        }
        int resultNum = 0;
        Random r = new Random();
        int num = r.nextInt(1000);
        String deviceIdNew = deviceId + num;
        String key = "findUserBuryingPointIsNew::ballVideos:cacheAble:findUserBuryingPointIsNew:" + deviceId;
        redisService.del(key);

        Map<String,Object> map = new HashMap<>();
        map.put("deviceId", deviceId);
        map.put("appPackage", appPackage);
        ClDeviceVo clDeviceVo = clDeviceMapper.getDeviceByDeviceIdAppPackage(map);
        if (clDeviceVo != null) {
            Map<String,Object> mapUpdate = new HashMap<>();
            mapUpdate.put("id", clDeviceVo.getId());
            mapUpdate.put("deviceId", deviceIdNew);
            resultNum = clDeviceMapper.updateDeviceId(mapUpdate);
            log.error("删除cl_device成功："+deviceId+", id:" + clDeviceVo.getId());
            List<ClUserVo> clUserVos = clUserMapper.findClUserList(map);
            if (clUserVos != null && clUserVos.size() > 0) {
                ClUserVo clUserVo = clUserVos.get(0);
                clUserVo.setDeviceId(deviceIdNew);
                clUserMapper.updateClUser(clUserVo);
                log.error("删除cl_user成功："+deviceId+", id:" + clUserVo.getId());
            }
            /*if (StringUtils.isNotEmpty(clDeviceVo.getDistinctId())) {
                Query query1 = Query.query(Criteria.where("distinct_id").is(clDeviceVo.getDistinctId()));
                mongoTemplate.remove(query1, "android_device_info");
                log.error("删除android_device_info成功："+deviceId+", distinct_id:" + clDeviceVo.getDistinctId());

                Query query2 = Query.query(Criteria.where("init_distinct_id").is(clDeviceVo.getDistinctId()));
                mongoTemplate.remove(query2, "android_user_idmap");
                log.error("删除android_user_idmap成功："+deviceId+", distinct_id:" + clDeviceVo.getDistinctId());
            }*/
        } else {
            log.error("根据设备号删除账号信息失败：无设备信息："+deviceId);
        }
        return resultNum;
    }
}
