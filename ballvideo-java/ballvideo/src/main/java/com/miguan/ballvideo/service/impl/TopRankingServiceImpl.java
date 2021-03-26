package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.base.core.exception.CommonException;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.mapper.HotListMapper;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.TopRankingService;
import com.miguan.ballvideo.service.VideoCacheService;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.HotListConfVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopRankingServiceImpl implements TopRankingService {

    @Resource
    private HotListMapper hotListMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoCacheService videoCacheService;

    @Resource
    private AdvertService advertService;

    @Resource
    private VideoAlbumMapper videoAlbumMapper;

    @Override
    public void setHotListToRedis(Integer type){
        List<HotListVo> list = hotListMapper.queryHotListByType(type);
        if (list.size() < 50){
            throw new CommonException("热榜数据不足50条");
        }
        if (VideoContant.HOUR_HOT_LIST_CODE.equals(type)){
            //实时热播数据
            redisService.set(RedisKeyConstant.HOUR_HOT_LIST_KEY, JSONObject.toJSONString(list), -1);
        }else if (VideoContant.DAY_HOT_LIST_CODE.equals(type)){
            List<HotListVo> topTreeList=list.subList(0, 3);
            //24小时热播前三条数据
            redisService.set(RedisKeyConstant.TOP_THREE_HOT_LIST_KEY, JSONObject.toJSONString(topTreeList), -1);
            //24小时热播数据
            redisService.set(RedisKeyConstant.DAY_HOT_LIST_KEY, JSONObject.toJSONString(list), -1);
        }
    }

    @Override
    public HotListConfVo getHotListConf() {
        return hotListMapper.getHotListConf();
    }

    @Override
    public List<HotListVo> queryTopThreeHotList(CommonParamsVo params) {
        List<HotListVo> list = new ArrayList<>();
        String value = redisService.get(RedisKeyConstant.TOP_THREE_HOT_LIST_KEY, String.class);
        if (StringUtils.isNotBlank(value)){
             list = JSON.parseArray(value, HotListVo.class);
            //根据用户视频关联表判断是否收藏
            videoCacheService.getVideosCollection2(list, params.getUserId());
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption3(list,videoAlbumVos,params.getMobileType(),params.getAppVersion());
        }
        return list;
    }

    @Override
    public List<HotListVo> queryHotListByType(CommonParamsVo params,Integer type) {
        List<HotListVo> list = new ArrayList<>();
        String value = "";
        if (VideoContant.HOUR_HOT_LIST_CODE.equals(type)){
            //实时热播数据
            value = redisService.get(RedisKeyConstant.HOUR_HOT_LIST_KEY, String.class);
        }else if (VideoContant.DAY_HOT_LIST_CODE.equals(type)){
            //24小时热播数据
            value = redisService.get(RedisKeyConstant.DAY_HOT_LIST_KEY, String.class);
        }
        if (StringUtils.isNotBlank(value)){
            list = JSON.parseArray(value, HotListVo.class);
            //激励视频查询
            String incentiveVideoSwitch = "";
            String incentiveVideoRate = "";
            Map<String, Object> advParam = new HashMap<>();
            advParam.put("queryType","position");
            advParam.put("positionType","incentiveVideoPosition");
            advParam.put("mobileType",params.getMobileType());
            advParam.put("appPackage",params.getAppPackage());
            List<AdvertCodeVo> advertCodeVoList = advertService.getAdvertInfoByParams(null, advParam, 3);
            if (CollectionUtils.isNotEmpty(advertCodeVoList)) {
                incentiveVideoRate = advertCodeVoList.get(0).getSecondLoadPosition() == null ? "0" : advertCodeVoList.get(0).getSecondLoadPosition() + "";
                incentiveVideoSwitch = advertCodeVoList.get(0).getMaxShowNum() == null ? "0" : advertCodeVoList.get(0).getMaxShowNum() + "";
            }
            if (Constant.OPENSTR.equals(incentiveVideoSwitch)) {
                for (HotListVo hotListVo : list) {
                    if (hotListVo.getIncentiveVideo() == 1) {
                        hotListVo.setIncentiveRate(Double.parseDouble(incentiveVideoRate));
                    }
                }
            }
            //根据用户视频关联表判断是否收藏
            videoCacheService.getVideosCollection2(list, params.getUserId());
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption3(list,videoAlbumVos,params.getMobileType(),params.getAppVersion());
        }
        return list;
    }

}
