package com.miguan.ballvideo.service.impl;


import com.miguan.ballvideo.common.util.adv.AdvFieldType;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.dynamicquery.Dynamic2Query;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.AdvertFlowService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class AdvertFlowServiceImpl implements AdvertFlowService {

    @Resource
    private Dynamic2Query dynamic2Query;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private RedisService redisService;

    @Resource
    private DynamicQuery dynamicQuery;

    @Override
    public List<AdvertCodeVo> commonSearch(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        List<AdvertCodeVo> advertCodeVos = getAdvertsByParams(param, AdvFieldType.All);
        if(CollectionUtils.isEmpty(advertCodeVos))return null;
        if (advertCodeVos.get(0).getComputer() == 1) {
            //手动配比
            return AdvUtils.computerAndSort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 2){
            //手动排序
            return AdvUtils.sort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 3){
            //自动排序
            return AdvUtils.sort(advertCodeVos);
        } else {
            return AdvUtils.sort(advertCodeVos);
        }
    }

    @Override
    public Map<String, Integer> judgeValidity(Map<String, Object> param) {
        StringBuffer sql = new StringBuffer("select ");
        sql.append("(SELECT count(1) FROM `ad_app` where app_id = ?) appCnt , ");
        sql.append("(SELECT count(1) FROM `ad_app` where secret_key = ?) secKeyCnt , ");
        sql.append("(SELECT count(1) FROM `ad_advert_position` where position_type = ?) posKeyCnt ");
        sql.append("from dual");
        List<Map<String,Integer>> lst = dynamic2Query.nativeQueryListMap(sql.toString(),param.get("appId"),param.get("secretkey"),param.get("poskey"));
        return lst.get(0);
    }

    /**
     * 查询一个或多个广告位的广告信息
     * @param param
     * @param fieldType
     * @return
     */
    public List<AdvertCodeVo> getAdvertInfoByParams(Map<String, Object> param, int fieldType) {
        String key = AdvUtils.filter(param);
        String json = redisService.get(key);
        if(RedisKeyConstant.EMPTY_VALUE.equals(json)){
            return null;
        }
        List<AdvertCodeVo> sysVersionVos = dynamic2Query.getAdversWithCache(param,fieldType);
        if(CollectionUtils.isEmpty(sysVersionVos)){
            redisService.set(key, RedisKeyConstant.EMPTY_VALUE, RedisKeyConstant.EMPTY_VALUE_SECONDS);
            return null;
        }else{
            return sysVersionVos;
        }
    }

    /**
     * 查询广告信息
     * @param param
     * @param fieldType
     * @return
     */
    public List<AdvertCodeVo> getAdvertsByParams(Map<String, Object> param, int fieldType) {
        //魔方后台-广告总开关:true禁用，false非禁用
        if (toolMofangService.stoppedByMofang(param)) {
            return null;
        }
        return getAdvertInfoByParams(param,fieldType);
    }

}
