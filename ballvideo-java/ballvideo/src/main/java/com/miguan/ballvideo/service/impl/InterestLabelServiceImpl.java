package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.enums.InterestLabelEnum;
import com.miguan.ballvideo.entity.ClInterestLabel;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.mapper.ClInterestLabelMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.InterestLabelService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.UserBuriedPointService;
import com.miguan.ballvideo.vo.SaveLabelInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InterestLabelServiceImpl implements InterestLabelService {

    @Resource
    private ClInterestLabelMapper clInterestLabelMapper;
    @Resource
    private UserBuriedPointService userBuriedPointService;
    @Resource
    private RedisService redisService;

    @Override
    public int saveLabelInfo(SaveLabelInfo labelInfo,String publicInfo) {
        if ("-1".equals(labelInfo.getLabelId())) {
            String key = RedisKeyConstant.INTERESTLABEL_KEY + labelInfo.getDeviceId();
            redisService.set(key, "1", RedisKeyConstant.APP_TOKEN_SECONDS);
            return 1;
        }
        PublicInfo pbInfo  = new PublicInfo(publicInfo);
        String uuid = pbInfo.getUuid();
        if (StringUtils.isEmpty(uuid)) {
            return 0;
        }
        ClInterestLabel label = new ClInterestLabel();
        BeanUtils.copyProperties(labelInfo, label);
        String[] labelIds = label.getLabelId().split(",");
        StringBuilder labelNames = new StringBuilder();
        StringBuilder catIds = new StringBuilder();
        for (int i=0;i<labelIds.length;i++) {
            String labelName = InterestLabelEnum.getName(Integer.valueOf(labelIds[i]));
            labelNames.append(",");
            labelNames.append(labelName);
            String catId = InterestLabelEnum.getCatId(Integer.valueOf(labelIds[i]));
            catIds.append(",");
            catIds.append(catId);
        }
        String labelNameStr = labelNames.toString().substring(1);
        String catIdStr = catIds.toString().substring(1);
        label.setLabelName(labelNameStr);
        label.setCatId(catIdStr);
        label.setUuid(uuid);
        String key = RedisKeyConstant.INTERESTLABEL_KEY + labelInfo.getDeviceId();
        redisService.set(key, "1", RedisKeyConstant.APP_TOKEN_SECONDS);
        return clInterestLabelMapper.saveLabelInfo(label);
    }

    @Override
    public boolean getLabelInfo(String deviceId, String channelId) {
        Integer newOrOld = userBuriedPointService.judgeUser(deviceId,channelId);
        String key = RedisKeyConstant.INTERESTLABEL_KEY + deviceId;
        String saveInfo = redisService.get(key);
        if (newOrOld != 10 || StringUtils.isNotEmpty(saveInfo)) {
            return false;
        }
        return true;
    }
}
