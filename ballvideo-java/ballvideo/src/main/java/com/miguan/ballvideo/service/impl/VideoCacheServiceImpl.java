package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.mapper.*;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.SmallVideosVo;
import com.miguan.ballvideo.vo.VideosCatVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xujinbang
 * @date 2019/11/9.
 */
@Slf4j
@Service
public class VideoCacheServiceImpl implements VideoCacheService {

    @Resource(name="redisDB8Service")
    private RedisDB8Service redisDB8Service;
    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private SmallVideosMapper smallVideosMapper;

    @Resource
    private AdvertMapper advertMapper;

    @Resource
    private VideosCatMapper videosCatMapper;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private VideosCatService videosCatService;

    @Resource
    private ClUserVideosMapper clUserVideosMapper;

    @Resource
    private ClUserService clUserService;

    @Override
    public Map<Long, VideosCatVo> getVideosCatMap(String type) {
        List<VideosCatVo> videosCatVos = videosCatMapper.firstVideosCatList(type);
        if (CollectionUtils.isEmpty(videosCatVos)){
            return new HashMap<>();
        }
        return videosCatVos.stream().collect(Collectors.toMap(VideosCatVo::getId,v -> v));
    }

    @Override
    public void fillParams(List<Videos161Vo> firstVideos) {
        //VideoUtils.setLoveAndWatchNum(firstVideos);
        //赋值分类名称，神策埋点需要
        VideoUtils.setCatName(firstVideos, null, getVideosCatMap(VideoContant.FIRST_VIDEO_CODE));
    }

    @Override
    public void getVideosCollection(List<Videos161Vo> firstVideos,String userId) {
        if (CollectionUtils.isEmpty(firstVideos)){
            return;
        }
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos(firstVideos);
        //根据用户视频关联表判断是否收藏
        if (userId != null && !userId.equals("0")) {
            List<Long> videoIds = firstVideos.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<Long> list = clUserVideosMapper.queryCollection(userId, videoIds);
            for (int i = 0; i < list.size(); i++) {
                Long aLong =  list.get(i);
                for (Videos161Vo esVideo : firstVideos) {
                    Long videoId = esVideo.getId();
                    if (videoId.equals(aLong)){
                        esVideo.setCollection("1");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void getVideosCollection2(List<HotListVo> hotList, String userId) {
        if (CollectionUtils.isEmpty(hotList)){
            return;
        }
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos2(hotList);
        //根据用户视频关联表判断是否收藏
        if (userId != null && !userId.equals("0")) {
            List<Long> videoIds = hotList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<Long> list = clUserVideosMapper.queryCollection(userId, videoIds);
            for (int i = 0; i < list.size(); i++) {
                Long aLong =  list.get(i);
                for (HotListVo esVideo : hotList) {
                    Long videoId = esVideo.getId();
                    if (videoId.equals(aLong)){
                        esVideo.setCollection("1");
                        break;
                    }
                }
            }
        }
    }
    /**
     * 考虑到大部分人没登录，针对没登录情况做4小时缓存
     * @param params
     * @return
     */
    @Override
    public List<Videos161Vo> getFirstVideos161(Map<String, Object> params,int count) {
        final List<Videos161Vo> firstVideosList = new ArrayList<>();

        Object excludeId = params.get("excludeId");
        Object videoType = params.get("videoType");
        Object catId = params.get("catId");
        Object otherCatIds = params.get("otherCatIds");
        Object id = params.get("id");
        Object state = params.get("state");
        Object gatherIds = params.get("gatherIds");
        Object appPackage = params.get("appPackage");


        if(catId==null){
            //catId为空，随机获取一个 add shixh0430
            List<String> catIds = videosCatService.getCatIdsByStateAndType(Constant.open,VideoContant.FIRST_VIDEO_CODE);
            Collections.shuffle(catIds);
            catId = catIds.get(0);
            params.put("catId",catId);
        }

        final StringBuilder stringBuilder = new StringBuilder(RedisKeyConstant.NEWFIRSTVIDEO161_KEY);
        stringBuilder.append(excludeId != null ? excludeId.toString() : "@");
        stringBuilder.append(videoType != null ? videoType.toString() : "@");
        stringBuilder.append(catId != null ? catId.toString() : "@");
        stringBuilder.append(otherCatIds != null ? otherCatIds.toString() : "@");
        stringBuilder.append(id != null ? id.toString() : "@");
        stringBuilder.append(state != null ? state.toString() : "@");
        stringBuilder.append(gatherIds != null ? gatherIds.toString() : "@");
        stringBuilder.append(appPackage != null ? appPackage.toString() : "@");
        String key = stringBuilder.toString();

        if(!redisDB8Service.exits(key)) {
            List<Videos161Vo> list = firstVideosMapper.findFirstVideosList161(params);
            if(list.isEmpty()) {
                return firstVideosList;
            }
            String[] value = list.stream().collect(Collectors.toMap(Videos161Vo::getId,e -> JSONObject.toJSONString(e))).values().toArray(new String[list.size()]);
            redisDB8Service.sadd(key,RedisKeyConstant.NEWFIRSTVIDEO161_SECONDS,value);
        }
        redisDB8Service.randomValue(key,count).forEach(e -> firstVideosList.add(JSONObject.parseObject(e,Videos161Vo.class)));
        return firstVideosList;
    }

    /**
     * 考虑到大部分人没登录，针对没登录情况做4小时缓存
     * @param params
     * @return
     */
    @Override
    public List<FirstVideos> getFirstVideos(Map<String, Object> params,int count) {
        List<FirstVideos> firstVideosList = new ArrayList<>();

        Object excludeId = params.get("excludeId");
        Object videoType = params.get("videoType");
        String catId = MapUtils.getString(params, "catId");
        Object otherCatIds = params.get("otherCatIds");
        Object id = params.get("id");
        Object state = params.get("state");
        Object appPackage = params.get("appPackage");

        if(StringUtils.isEmpty(catId)){
            //catId为空，随机获取一个 add shixh0430
            List<String> catIds = videosCatService.getCatIdsByStateAndType(Constant.open,VideoContant.FIRST_VIDEO_CODE);
            if(otherCatIds!=null && StringUtils.isNotEmpty(otherCatIds+"")){
                for(Long id_cat:(List<Long>)otherCatIds){
                    catIds.remove(String.valueOf(id_cat));
                }
            }
            if(CollectionUtils.isEmpty(catIds)){
                catId = "251";
            }else{
                Collections.shuffle(catIds);
                catId = catIds.get(0);
            }
            params.put("catId",catId);
        }
        final StringBuilder stringBuilder = new StringBuilder(RedisKeyConstant.NEWFIRSTVIDEO_KEY);
        stringBuilder.append(excludeId != null ? excludeId.toString() : "@");
        stringBuilder.append(videoType != null ? videoType.toString() : "@");
        stringBuilder.append(StringUtils.isNotEmpty(catId) ? catId : "@");
        stringBuilder.append(otherCatIds != null ? otherCatIds.toString() : "@");
        stringBuilder.append(id != null ? id.toString() : "@");
        stringBuilder.append(state != null ? state.toString() : "@");
        stringBuilder.append(appPackage != null ? appPackage.toString() : "@");
        String key = stringBuilder.toString();

        if(!redisDB8Service.exits(key)) {
            List<FirstVideos> list = firstVideosMapper.findFirstVideosList(params);
            if(list.isEmpty()) {
                return firstVideosList;
            }
            String[] value = list.stream().collect(Collectors.toMap(FirstVideos::getId,e -> JSONObject.toJSONString(e))).values().toArray(new String[list.size()]);
            redisDB8Service.sadd(key,RedisKeyConstant.NEWFIRSTVIDEO_SECONDS,value);
        }
        redisDB8Service.randomValue(key,count).forEach(e -> firstVideosList.add(JSONObject.parseObject(e,FirstVideos.class)));
        return firstVideosList;
    }

    /**
     * 缓存广告信息，2分钟内直接从缓存获取
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> getAdvertList(Map<String, Object> param,int count) {
        //版本2.2.0以上，市场审核开关开启，屏蔽所有广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return null;
        }
        List<AdvertVo> advers = advertMapper.queryAdertList(param);
        advers = advertOldService.getAdvertsByChannel(advers, param);
        return AdvUtils.computer(advers,count);
    }

    /**
     * 获取基础广告信息
     *
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> getBaseAdvertList(Map<String, Object> param) {
        //版本2.2.0以上，市场审核开关开启，屏蔽所有广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return null;
        }
        List<AdvertVo> advers = advertMapper.queryAdertList(param);
        advers = advertOldService.getAdvertsByChannel(advers, param);
        return advers;
    }

    /**
     * 考虑到大部分人没登录，针对没登录情况做1小时缓存
     * @param params
     * @return
     */
    @Override
    public List<SmallVideosVo> getSmallVideos(Map<String, Object> params,int count) {
        return smallVideosMapper.findSmallVideosList(params);
    }


}


