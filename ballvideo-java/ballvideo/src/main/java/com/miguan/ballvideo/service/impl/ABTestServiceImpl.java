package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.AbTestConfig;
import com.miguan.ballvideo.entity.AbTestUser;
import com.miguan.ballvideo.repositories.ABTestConfigJpaRepository;
import com.miguan.ballvideo.repositories.ABTestUserJpaRepository;
import com.miguan.ballvideo.service.ABTestService;
import com.miguan.ballvideo.vo.AbTestUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("aBTestService")
public class ABTestServiceImpl implements ABTestService {

    @Resource
    private ABTestUserJpaRepository aBTestUserJpaRepository;
    @Resource
    private ABTestConfigJpaRepository aBTestConfigJpaRepository;

    private String AUSER = "A";
    private String BUSER = "B";

    private String SVIDEOBADGE = "s_video_badge";
    private String SVIDEODEFAULT = "s_video_default";
    private String APPSTARTDEFAULT = "app_start_default";
    private String INDEXVIDEOSHOW = "index_video_show";

    @Override
    @Transactional
    public AbTestUserVo findABUserInfo(String deviceId, String channelId) {
        AbTestUserVo abTestUserVo = new AbTestUserVo();
        List<AbTestConfig> abTestConfigList = aBTestConfigJpaRepository.findAbTestConfig(channelId);
        if (CollectionUtils.isNotEmpty(abTestConfigList)) {
            for (AbTestConfig abTestConfig : abTestConfigList) {
                getAbTestUserVo(deviceId, abTestConfig, abTestUserVo);
            }
        }
        return abTestUserVo;
    }

    /**
     * 获取AB测试用户信息
     * @param deviceId
     * @param abTestConfig
     * @return
     */
    private void getAbTestUserVo(String deviceId, AbTestConfig abTestConfig, AbTestUserVo abTestUserVo) {
        AbTestUser abTestUser = new AbTestUser();
        abTestUser.setDeviceId(deviceId);
        List<AbTestUser> abTestUserList = aBTestUserJpaRepository.findByDeviceIdAndAbTestConfigId(abTestUser.getDeviceId(),abTestConfig.getId());
        if (CollectionUtils.isNotEmpty(abTestUserList)) {
            abTestUser = abTestUserList.get(0);
        } else {
            double rate = abTestConfig.getBUserPercentage() == null ? 0.00 : (abTestConfig.getBUserPercentage().doubleValue() / 100);
            //根据比例计算出A用户或者B用户
            String abUser = percentageRandom(rate);
            abTestUser.setAbUser(abUser);
            abTestUser.setAbTestConfigId(abTestConfig.getId());
            abTestUser.setCreateTime(new Date());
            //新增AB测试用户
            addABTestUser(abTestConfig, abTestUser, abUser);
        }
        setABTestResult(abTestConfig, abTestUserVo, abTestUser);
    }

    /**
     * 用户AB测试结果返回给前端
     * @param abTestConfig
     * @param abTestUserVo
     * @param abTestUser
     */
    private void setABTestResult(AbTestConfig abTestConfig, AbTestUserVo abTestUserVo, AbTestUser abTestUser) {
        if (SVIDEOBADGE.equals(abTestConfig.getName())) {
            if (BUSER.equals(abTestUser.getAbUser())) {
                abTestUserVo.setSmallVideoBadge("1");
            } else {
                abTestUserVo.setSmallVideoBadge("0");
            }
        } else if (SVIDEODEFAULT.equals(abTestConfig.getName())) {
            if (BUSER.equals(abTestUser.getAbUser())) {
                abTestUserVo.setSmallVideoDefault("1");
            } else {
                abTestUserVo.setSmallVideoDefault("0");
            }
        } else if (APPSTARTDEFAULT.equals(abTestConfig.getName())) {
            if (BUSER.equals(abTestUser.getAbUser())) {
                abTestUserVo.setAppStartDefault("1");
            } else {
                abTestUserVo.setAppStartDefault("0");
            }
        } else if (INDEXVIDEOSHOW.equals(abTestConfig.getName())) {
            if (BUSER.equals(abTestUser.getAbUser())) {
                abTestUserVo.setIndexVideoShow("1");
            } else {
                abTestUserVo.setIndexVideoShow("0");
            }
        }
    }

    /**
     * 新增AB测试用户
     * @param abTestConfig
     * @param abTestUser
     * @param abUser
     */
    private void addABTestUser(AbTestConfig abTestConfig, AbTestUser abTestUser, String abUser) {
        if (BUSER.equals(abUser) && abTestConfig.getBUserNum() < abTestConfig.getTotalBUserNum()) {
            //B面已灰度用户量小于B面用户量上限，且该设备号计算为B面用户则B面已灰度用户量加1
            aBTestConfigJpaRepository.updateAbUserConfig(abTestConfig.getId());
        } else {
            //B面已灰度用户量达到上限，则不再增加B面用户
            abTestUser.setAbUser(AUSER);
        }
        try {
            aBTestUserJpaRepository.save(abTestUser);
        } catch (Exception e) {
            log.error("AB测试用户新增失败,[{}]", e.getMessage());
        }
    }

    /**
     * 根据rate比例计算出A用户或者B用户
     * @param rate
     * @return
     */
    private String percentageRandom(double rate) {
        double randomNumber = Math.random();
        if (randomNumber > 0 && randomNumber <= rate) {
            return BUSER;
        } else {
            return AUSER;
        }
    }
}
