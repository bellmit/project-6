package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.util.CacheUtil;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisDB8Service;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.SysService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


/**
 * @Author shixh
 * @Date 2020/3/23
 **/
@Slf4j
@Service
public class SysServiceImpl implements SysService {

    @Resource
    private RedisService redisService;

    @Resource(name="redisDB8Service")
    private RedisDB8Service redisDB8Service;


    @Resource
    private DynamicQuery dynamicQuery;

    @Override
    public void delRedis(String redisKey) {
        if (StringUtils.isBlank(redisKey) || redisKey.contains("*")){
            log.info("参数不存在或者参数带*，删除失败");
            return;
        }
          //删除视频相关缓存
          if (RedisKeyConstant.NEWFIRSTVIDEO161_KEY.contains(redisKey)) {
               //PHP后端操作会导致缓存雪崩，先注释掉 add shixh 0518
                /*Set<String> keys1 = redisDB8Service.keys(RedisKeyConstant.NEWFIRSTVIDEO161_KEY + "*");
                for (String key : keys1) {
                    redisDB8Service.del(key);
                }
                Set<String> keys2 = redisDB8Service.keys(RedisKeyConstant.NEWFIRSTVIDEO_KEY + "*");
                for (String key : keys2) {
                    redisDB8Service.del(key);
                }
                */
                Set<String> keys3 = redisService.keys(RedisKeyConstant.REQUEST_CACHE_KEY + "com.miguan.ballvideo.controller.VideoGatherEsController*");
                for (String key : keys3) {
                    redisService.del(key);
                }
            }else{
                //根据key模糊查询删除
                Set<String> set = redisService.keys("*"+redisKey + "*");
                for (String key : set) {
                    redisService.del(key);
                }
            }
    }

    @Override
    public void updateAdConfigCache() {
/*        List<AdPositionConfig> adPositionConfigList = thirdDataService.getAdPositionConfigList();
        Map<String, AdPositionConfig> appleMap = adPositionConfigList.stream().collect(Collectors.toMap(AdPositionConfig::getKeywordMobileType, a -> a,(k1, k2) -> k1));
        CacheUtil.initAdPositionConfigs(appleMap);*/

    }

    @Override
    public void updateAdLadderCache() {
        String sql = "select id,banner_position_id,version_start,version_end,keyword_mobileType,app_package from banner_price_ladder where state = 1 ";
        List<BannerPriceLadderVo> bannerPriceLadders = dynamicQuery.nativeQueryList(BannerPriceLadderVo.class,sql);
        CacheUtil.initBannerPriceLadders(bannerPriceLadders);
    }
}
