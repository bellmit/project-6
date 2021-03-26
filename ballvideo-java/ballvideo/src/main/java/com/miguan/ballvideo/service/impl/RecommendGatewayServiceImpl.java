package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.entity.recommend.UserFeature;
import com.miguan.ballvideo.service.FeatureService;
import com.miguan.ballvideo.service.RecommendGatewayService;
import com.miguan.ballvideo.service.recommend.RecommendVideosServiceImpl2Asyn;
import com.miguan.ballvideo.service.recommend.RecommendVideosServiceImpl3Asyn;
import com.miguan.ballvideo.service.recommend.RecommendVideosServiceImplAsyn;
import com.miguan.ballvideo.service.recommendlower.LowerRecommendVideosServiceImplAsyn;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**推荐网关service
 * @author zhongli
 * @date 2020-09-03 
 *
 */
@Service
@Slf4j
public class RecommendGatewayServiceImpl implements RecommendGatewayService {
    @Autowired
    private RecommendVideosServiceImplAsyn recommendVideosServiceImplAsyn;
    @Autowired
    private RecommendVideosServiceImpl2Asyn recommendVideosServiceImpl2Asyn;
    @Autowired
    private RecommendVideosServiceImpl3Asyn recommendVideosServiceImpl3Asyn;
    @Autowired
    private LowerRecommendVideosServiceImplAsyn lowerRecommendVideosServiceImplAsyn;
    @Resource
    private FeatureService featureService;
    @Autowired
    private UserFeature userFeature;

    @Override
    public FirstVideos161Vo getRecommendVideos(VideoParamsDto dto, String publicInfo, AbTestAdvParamsVo queueVo) {
        //是否符合版本
        boolean isVS = !VersionUtil.compareIsHigh(Constant.APPVERSION_280, dto.getAppVersion());
        if (isVS && StringUtils.isNotBlank(publicInfo)) {
            try {
                PublicInfo pbInfo  = new PublicInfo(publicInfo);
                userFeature.initUserFeature(pbInfo);
                dto.setUserFeature(userFeature);
                dto.setPublicInfoStr(publicInfo);
                dto.setDistincId(pbInfo.getDistinctId());
                dto.setUuid(pbInfo.getUuid());
            } catch (Exception e) {
                log.info("新推荐失败:{}", e.getMessage());
                isVS = false;
            }
        }
        if (StringUtils.isBlank(dto.getUuid())) {
            isVS = false;
        }
        if (isVS){
            checkSubABTest(dto);
            FirstVideos161Vo resultList = null;
            if(isABTestUser(dto)) {
                //是否使用新推荐算法
                if (log.isDebugEnabled()) {
                    log.debug("用户uuid= 使用了新推荐0.3", dto.getUuid());
                }
                resultList = recommendVideosServiceImpl3Asyn.getRecommendVideos(dto, queueVo);
            } else {
                //是否使用新推荐算法
                if (log.isDebugEnabled()) {
                    log.debug("用户uuid= 使用了新推荐0.2", dto.getUuid());
                }
                resultList =  recommendVideosServiceImpl2Asyn.getRecommendVideos(dto, queueVo);
            }
            featureService.saveFeatureToRedis(dto.getUserFeature(), resultList);
            return resultList;
        }
        return lowerRecommendVideosServiceImplAsyn.getRecommendVideos(dto, queueVo);
    }

    /**
     * 是否使用新推荐算法(使用取模算法，把deviceId进行md5加密后，在转成hashcode，用这个hashcode进行取模，如果取模后值是0，则使用新推荐算法)
     * @return true：使用新推荐算法，false：使用旧推荐算法
     */
    private boolean isABTestUser(VideoParamsDto dto) {
        //判定是不是指定测试用户
        Object use_recommend_test = Global.getObject("use_recommend_test");
        if (use_recommend_test != null && use_recommend_test.toString().contains(dto.getUuid())) {
            return true;
        }
        //使用新推荐算法mod值。如果值为0，则都用旧算法；如果值为1，则都使用新算法
        int useRecommendMod = Global.getInt("use_recommend_mod");
        if (useRecommendMod == 0) {
            return false;
        }
        if (useRecommendMod == 1) {
            return true;
        }
        try {
            String firtsalt = Global.getValue("use_recommend_salt");
            String md5 = DigestUtils.md5Hex(dto.getUuid().concat(firtsalt)).substring(16, 16 + 8);
            long t = Long.parseLong(md5, 16);
            if (t % useRecommendMod == 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("A/B推荐用户取模异常", e);
        }
        return false;
    }

    public void checkSubABTest(VideoParamsDto dto) {
        try {
            //判定是不是指定测试用户
            Object use_recommend_test = Global.getObject("use_recommend_test");
            if (use_recommend_test != null && use_recommend_test.toString().contains(dto.getUuid())) {
                dto.setABTest(true);
                return;
            }
            int subuseRecommendMod = Global.getInt("use_recommend_submod");
            if (subuseRecommendMod == 1) {
                dto.setABTest(true);
            } else if (subuseRecommendMod != 0) {
                String secondsalt = Global.getValue("use_recommend_subsalt");
                String md5 = DigestUtils.md5Hex(dto.getUuid().concat(secondsalt)).substring(16, 16 + 8);
                long t = Long.parseLong(md5, 16);

                int subuseRecommendModValue = Global.getInt("use_recommend_submod_value");
                log.warn("{} 推荐0.2 SUB-AB 用户计算>>{},{},{}", dto.getUuid(), t, subuseRecommendMod, subuseRecommendModValue);
                dto.setABTest(t % subuseRecommendMod == subuseRecommendModValue);
            }
            //是否使用新推荐算法
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2 SUB-AB 用户是否命中：{}", dto.getUuid(), dto.isABTest());
            }
        } catch (Exception e) {
            log.error("Sub A/B推荐用户取模异常", e);
        }
    }
}
