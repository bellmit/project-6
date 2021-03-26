package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.entity.UserLabelGrade;
import com.miguan.ballvideo.repositories.UserLabelGradeJpaRepository;
import com.miguan.ballvideo.service.UserLabelGradeService;
import com.miguan.ballvideo.vo.BuryingActionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("userLabelGradeService")
public class UserLabelGradeServiceImpl implements UserLabelGradeService {

    @Resource
    private UserLabelGradeJpaRepository userLabelGradeJpaRepository;

    @Override
    public void updateUserLabelGrade(String deviceId, Long catId, String actionId) {
        if (BuryingActionType.XY_VIDEO_PLAY.equals(actionId)) {
            actionId = "video_play";
        } else if (BuryingActionType.XY_VIDEO_PLAYOVER.equals(actionId)) {
            actionId = "video_all_play";
        } else if (BuryingActionType.XY_VIDEO_PRAISE.equals(actionId)) {
            actionId = "video_praise";
        } else if (BuryingActionType.XY_VIDEO_COMMENT.equals(actionId)) {
            actionId = "video_comment";
        } else if (BuryingActionType.XY_VIDEO_COLLECT.equals(actionId)) {
            actionId = "video_collect";
        } else if (BuryingActionType.SHARE_WAY.equals(actionId)) {
            actionId = "video_share";
        }
        String videoRatioStr = Global.getValue(actionId);
        if (StringUtils.isNotBlank(videoRatioStr)) {
            Double videoRatio = Double.parseDouble(videoRatioStr == null ? "0" : videoRatioStr);
            UserLabelGrade userLabelGrade = userLabelGradeJpaRepository.getUserLabelGrade(deviceId, catId);
            if (userLabelGrade == null) {
                userLabelGrade = new UserLabelGrade();
                userLabelGrade.setDeviceId(deviceId);
                userLabelGrade.setCatId(catId);
                userLabelGrade.setCatGrade(videoRatio);
                try {
                    userLabelGradeJpaRepository.save(userLabelGrade);
                } catch (Exception e) {
                    log.error(e.getMessage()+",save params:" + JSON.toJSONString(userLabelGrade));
                    log.error("用户标签分唯一索引重复："+deviceId+"-"+userLabelGrade.getCatId());
                }
            } else {
                userLabelGrade.setCatGrade(userLabelGrade.getCatGrade() + videoRatio);
                userLabelGradeJpaRepository.save(userLabelGrade);
            }
        }
    }
}
