package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.PackageUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoCacheUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.UserLabelDefault;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.service.recommend.FindRecommendEsServiceImpl;
import com.miguan.ballvideo.service.recommend.IPService;
import com.miguan.ballvideo.service.recommendlower.LowerRecommendVideosServiceImplAsyn;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 推荐接口
 * @author laiyd
 * @date 2020-10-26
 *
 */
@Service
@Slf4j
public class RecommendVideoServiceImpl implements RecommendVideoService {

    @Resource
    private VideosCatMapper videosCatMapper;
    @Resource
    private MarketAuditService marketAuditService;
    @Resource
    private LowerRecommendVideosServiceImplAsyn lowerRecommendVideosServiceImplAsyn;
    @Resource
    private PredictService predictService;
    @Resource
    private UserLabelDefaultService userLabelDefaultService;
    @Resource
    private IPService ipService;
    @Resource
    private AdvertService advertService;
    @Resource
    private FindRecommendEsServiceImpl findRecommendEsService;
    @Resource
    private ClUserVideosService clUserVideosService;
    @Resource
    private VideoCacheService videoCacheService;
    @Resource
    private ClUserService clUserService;
    @Resource
    private FirstVideosService firstVideosService;
    @Resource
    private RedisService redisService;
    @Resource
    private RedisDB8Service redisDB8Service;
    @Resource
    private VideoEsService videoEsService;
    @Resource
    private VideoGatherService videoGatherService;
    @Resource
    private VideoAlbumService albumService;
    @Resource
    private VideoAlbumMapper videoAlbumMapper;

    private final static String code = "code";
    private final static int success = 200;
    private final static String isCombine = "2";

    @Override
    public FirstVideos161Vo getRecommendVideoList(VideoParamsDto dto, String publicInfo, String abExp, String abIsol, AbTestAdvParamsVo queueVo) {
        boolean errorFlag = false;
        List<String> videoIdList = Lists.newArrayList();
        //是否符合版本
        boolean isVS = !VersionUtil.compareIsHigh(Constant.APPVERSION_280, dto.getAppVersion());
        if (isVS && StringUtils.isNotBlank(publicInfo)) {
            FirstVideoParamsVo paramsVo = getFirstVideoParamsVo(dto, publicInfo);
            paramsVo.setAbExp(abExp);
            paramsVo.setAbIsol(abIsol);
            try {
                long pt = System.currentTimeMillis();
                String result = predictService.getRecommendVideoIds(paramsVo, Constant.recommend1);
                log.warn("{} ,大数据推荐耗时：{},推荐结果：{}", dto.getDeviceId(), (System.currentTimeMillis() - pt), result);
                /*if ((System.currentTimeMillis() - pt) > Constant.TIMEOUT_CODE_MAX) {
                    log.error("{} ,大数据推荐耗时：{}", dto.getDeviceId(), (System.currentTimeMillis() - pt));
                }*/
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)){
                    String data = jsonObject.getString("data");
                    if (StringUtils.isNotEmpty(data)) {
                        List<Videos161Vo> firstVideos = getVideos161Vos(dto, queueVo, videoIdList, data);
                        return getFirstVideoResultList(dto, queueVo, firstVideos);
                    } else {
                        errorFlag = true;
                    }
                } else {
                    log.error("推荐接口请求大数据接口失败：{}",result);
                    errorFlag = true;
                }
            } catch (Exception e) {
                log.error("推荐接口请求接口失败：{}",e.getMessage(),e);
                errorFlag = true;
            }
        }
        if (errorFlag) {
            Object videoCache = VideoCacheUtils.getObject(Constant.videoCacheList1);
            return (FirstVideos161Vo)videoCache;
        }
        return lowerRecommendVideosServiceImplAsyn.getRecommendVideos(dto, queueVo);
    }

    /**
     * 视频列表和广告列表组装
     * @param dto
     * @param queueVo
     * @param firstVideos
     * @return
     */
    private FirstVideos161Vo getFirstVideoResultList(VideoParamsDto dto, AbTestAdvParamsVo queueVo, List<Videos161Vo> firstVideos) {
        FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        //是否返回广告:双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
        if(isCombine.equals(dto.getIsCombine())) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else {
            //随机2个广告
            String channelId = ChannelUtil.filter(dto.getChannelId());
            Map<String, Object> adverMap = new HashMap<>(10);
            adverMap.put("marketChannelId", channelId);
            adverMap.put("channelId", channelId);
            adverMap.put("appVersion", dto.getAppVersion());
            adverMap.put("positionType", dto.getPositionType());
            adverMap.put("mobileType", dto.getMobileType());
            adverMap.put("permission", dto.getPermission());
            adverMap.put("appPackage", PackageUtil.getAppPackage(dto.getAppPackage(), dto.getMobileType()));
            //V2.5.0走新广告逻辑
            packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, adverMap, queueVo);
        }
        //重新返回占比给前端
        firstVideosNewVo.setVideoDuty("4,3,1");
        return firstVideosNewVo;
    }

    /**
     * 根据视频Id查询视频列表，排序
     * @param dto
     * @param queueVo
     * @param videoIdList
     * @param data
     * @return
     */
    private List<Videos161Vo> getVideos161Vos(VideoParamsDto dto, AbTestAdvParamsVo queueVo, List<String> videoIdList, String data) {
        List<Videos161Vo> resultVideos = Lists.newArrayList();
        Map<String, Object> dataMap = JSONObject.parseObject(data, Map.class);
        //精选视频
        Object selectedVideoObject = MapUtils.getObject(dataMap, "selectedVideo");
        List<String> selectedVideoIds = Lists.newArrayList();
        if (selectedVideoObject != null) {
            selectedVideoIds = (List<String>) selectedVideoObject;
            selectSpecialVideos(resultVideos,selectedVideoIds,videoIdList);
        }
        //普通视频
        Object videoObject = MapUtils.getObject(dataMap, "video");
        if (videoObject != null) {
            List<String> videoList = (List<String>)videoObject;
            videoIdList.addAll(videoList);
        }
        //激励视频配置参数
        Map<String, Object> incentiveMap = AdvUtils.getIncentiveInfo(dto.getMobileType(), dto.getAppPackage(), queueVo,1);
        Integer isShowIncentive = incentiveMap.get("isShowIncentive")==null? 0 : Integer.valueOf(incentiveMap.get("isShowIncentive").toString());
        Integer position = incentiveMap.get("position")==null? 0 : Integer.valueOf(incentiveMap.get("position").toString());
        String incentiveVideoRate = incentiveMap.get("incentiveVideoRate")==null? "0" : incentiveMap.get("incentiveVideoRate").toString();
        //激励视频
        if (isShowIncentive == 1) {
            Object incentiveVideoObject = MapUtils.getObject(dataMap, "incentiveVideo");
            if (incentiveVideoObject != null) {
                List<String> incentiveVideoIds = (List<String>) incentiveVideoObject;
                videoIdList.addAll(incentiveVideoIds);
            }
        }
        //根据视频id查询es
         List<Videos161Vo> esSearchVideos = findRecommendEsService.list(videoIdList, incentiveVideoRate);
        if (CollectionUtils.isEmpty(esSearchVideos)) {
            log.error("ES查询失败:getVideos161Vos,{}",JSON.toJSONString(dto));
        } else {
            resultVideos.addAll(esSearchVideos);
        }
        fillVideoInfo(dto.getUserId(), dto.getMobileType(), dto.getAppVersion(), resultVideos, selectedVideoObject, selectedVideoIds);
        List<Videos161Vo> firstVideos = new ArrayList<>();
        if (isNotEmpty(resultVideos)) {
            // 获取所有非激励视频
            List<Videos161Vo> incentiveVideoList = Lists.newArrayList();
            for (Videos161Vo vo : resultVideos) {
                if (vo.getIncentiveVideo() == null || vo.getIncentiveVideo().intValue() != 1) {
                    firstVideos.add(vo);
                } else {
                    incentiveVideoList.add(vo);
                }
            }
            //将激励视频其放到后台配置得相应位置
            boolean isNewUUID = redisService.exits(RedisKeyConstant.UUID_KEY + dto.getUuid());
            if (isNewUUID) {
                firstVideos.addAll(incentiveVideoList);
            } else {
                position = position > videoIdList.size() ? videoIdList.size() - 1 : position <= 0 ? 0 : (position - 1);
                firstVideos.addAll(position, incentiveVideoList);
            }
        }
        return firstVideos;
    }

    //组装视频信息
    private void fillVideoInfo(String userId, String mobileType, String appVersion, List<Videos161Vo> resultVideos, Object selectedVideoObject, List<String> selectedVideoIds) {
        if (isNotEmpty(resultVideos)) {
            if (selectedVideoObject != null) {
                //精选视频即使是激励视频也当普通视频
                setListOrder(selectedVideoIds, resultVideos);
                for (Videos161Vo vo : resultVideos) {
                    if (vo.getIncentiveVideo() != null && vo.getIncentiveVideo().intValue() == 1) {
                        if (selectedVideoObject.toString().contains(vo.getId().toString())) {
                            vo.setIncentiveVideo(0);
                        }
                    }
                }
            }
            //登录用户点赞信息
            fillLoginInfo(userId, resultVideos);
            videoCacheService.fillParams(resultVideos);
            //视频作者头像和名字取用户表（虚拟用户或者真是用户）
            clUserService.packagingUserAndVideos(resultVideos);
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption1(resultVideos, videoAlbumVos, mobileType, appVersion);
        }
    }

    //根据视频ID查询精选视频缓存信息
    private void selectSpecialVideos(List<Videos161Vo> resultVideos,List<String> selectedVideoIds,List<String> videoIdList){
        for (String videoId : selectedVideoIds) {
            String key = RedisKeyConstant.SPECIAL_VIDEO_LIST + videoId;
            String value = redisDB8Service.get(key);
            if (StringUtils.isNotEmpty(value)) {
                Videos161Vo vo = JSON.parseObject(value, Videos161Vo.class);
                //精选视频即使是激励视频也当普通视频
                vo.setIncentiveVideo(0);
                resultVideos.add(vo);
            } else {
                videoIdList.add(videoId);
            }
        }
    }

    /**
     * 组装查询条件
     * @param dto
     * @return
     */
    private FirstVideoParamsVo getFirstVideoParamsVo(VideoParamsDto dto, String publicInfo) {
        FirstVideoParamsVo paramsVo = new FirstVideoParamsVo();
        String defaultCatIds = getDefaultCatIds(dto.getChannelId());
        String catIds = getExcludeCatIds(dto);
        String[] ipInfo = ipService.getCurrentIpInfo();
        paramsVo.setDefaultCat(defaultCatIds);
        paramsVo.setExcludeCat(catIds);
        paramsVo.setIncentiveVideoNum("1");
        if (ipInfo != null) {
            paramsVo.setIp(ipInfo[0]);
        }
        paramsVo.setDeviceId(dto.getDeviceId());
        paramsVo.setPublicInfo(publicInfo);
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(dto.getChannelId(), dto.getAppVersion());
        if (marketAudit != null && marketAudit.getSensitiveState() == 1) {
            paramsVo.setSensitiveState("1");
        } else {
            paramsVo.setSensitiveState("0");
        }
        return paramsVo;
    }

    /**
     * 查询默认分类
     * @param channelId
     * @return
     */
    private String getDefaultCatIds(String channelId) {
        String defaultCatIds = "";
        UserLabelDefault userLabelDefault = userLabelDefaultService.getUserLabelDefault(channelId);
        if (userLabelDefault != null) {
            if (userLabelDefault.getCatId1() != null) {
                defaultCatIds = userLabelDefault.getCatId1() + "";
                if (userLabelDefault.getCatId2() != null) {
                    defaultCatIds = defaultCatIds + "," + userLabelDefault.getCatId2() + "";
                }
            } else {
                if (userLabelDefault.getCatId2() != null) {
                    defaultCatIds = userLabelDefault.getCatId2() + "";
                }
            }
        }
        return defaultCatIds;
    }

    /**
     * 查询屏蔽分类
     * @param dto
     * @return
     */
    private String getExcludeCatIds(VideoParamsDto dto) {
        StringBuilder catIds = new StringBuilder();
        //过滤市场审核开关屏蔽的分类
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(dto.getChannelId(), dto.getAppVersion());
        if (marketAudit != null && isNotBlank(marketAudit.getCatIds())) {
            catIds.append(marketAudit.getCatIds());
        }
        //屏蔽不生效分类
        List<String> catIdList = videosCatMapper.queryCatIdsList(dto.getAppPackage());
        if (CollectionUtils.isNotEmpty(catIdList)) {
            for (String catId : catIdList) {
                if (StringUtils.isNotEmpty(catIds)) {
                    catIds.append(",");
                    catIds.append(catId);
                } else {
                    catIds.append(catId);
                }
            }
        }
        return catIds.toString();
    }

    //V2.5.0走新广告与视频组合
    private void packagNewAdvertAndVideos(FirstVideos161Vo firstVideosNewVo, List<Videos161Vo> firstVideos, Map<String, Object> adverMap, AbTestAdvParamsVo queueVo) {
        List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, adverMap);
        List<FirstVideosVo> firstVideosVos = VideoUtils.packagingNewAdvert(advertCodeVos, firstVideos);
        firstVideosNewVo.setFirstVideosVos(firstVideosVos);
    }

    //用户登录，视频收藏，点赞信息
    private void fillLoginInfo(String userId, List<Videos161Vo> firstVideos) {
        boolean isLogin = StringUtils.isNotBlank(userId) && !"0".equals(userId);
        if (isLogin) {
            List<ClUserVideoInfoVo> list = clUserVideosService.findUserVideo(userId, firstVideos.stream().map(Videos161Vo::getId).collect(Collectors.toList()));
            if (!isEmpty(list)) {
                firstVideos.forEach(d -> {
                    Optional<ClUserVideoInfoVo> op = list.stream().filter(e -> e.getVideoId().longValue() == d.getId().longValue()).findFirst();
                    if (op.isPresent()) {
                        ClUserVideoInfoVo tmp = op.get();
                        d.setCollection(tmp.getCollection());
                        d.setLove(tmp.getLove());
                    }
                });
            }
        }
    }

    @Override
    public FirstVideos161Vo getRecommendOtherVideoList(AbTestAdvParamsVo queueVo, Map<String, Object> params, String publicInfo, String abExp, String abIsol) {
        boolean errorFlag = false;
        List<String> videoIdList = Lists.newArrayList();
        //是否符合版本
        boolean isVS = !VersionUtil.compareIsHigh(Constant.APPVERSION_280, params.get("appVersion").toString());
        if (isVS && StringUtils.isNotBlank(publicInfo)) {
            FirstVideoParamsVo paramsVo = getFirstVideoParamsVo(params, publicInfo);
            paramsVo.setAbExp(abExp);
            paramsVo.setAbIsol(abIsol);
            try {
                long pt = System.currentTimeMillis();
                String result = predictService.getRecommendVideoIds(paramsVo, Constant.recommend2);
                log.warn("{} ,大数据非推荐耗时：{},非推荐结果：{}", params.get("deviceId").toString(), (System.currentTimeMillis() - pt), result);
                /*if ((System.currentTimeMillis() - pt) > Constant.TIMEOUT_CODE_MAX) {
                    log.error("{},大数据非推荐耗时：{}", params.get("deviceId").toString(), (System.currentTimeMillis() - pt));
                }*/
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)){
                    String data = jsonObject.getString("data");
                    if (StringUtils.isNotEmpty(data)) {
                        List<Videos161Vo> firstVideos = getVideos161VosOther(params, videoIdList, data);
                        return getFirstOtherVideoResultList(params, queueVo, firstVideos);
                    } else {
                        errorFlag = true;
                    }
                } else {
                    log.error("非推荐接口请求大数据接口失败：{}",result);
                    errorFlag = true;
                }
            } catch (Exception e) {
                log.error("非推荐接口请求接口失败：{}",e.getMessage(),e);
                errorFlag = true;
            }
        }
        if (errorFlag) {
            Object videoCache = VideoCacheUtils.getObject(Constant.videoCacheList2);
            return (FirstVideos161Vo)videoCache;
        }
        return firstVideosService.firstVideosList161(queueVo, params);
    }

    /**
     * 视频列表和广告列表组装
     * @param params
     * @param queueVo
     * @param firstVideos
     * @return
     */
    private FirstVideos161Vo getFirstOtherVideoResultList(Map<String, Object> params, AbTestAdvParamsVo queueVo, List<Videos161Vo> firstVideos) {
        FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        //是否返回广告:双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
        String isCombineStr = MapUtils.getString(params, "isCombine");
        if(isCombine.equals(isCombineStr)) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else {
            //V2.5.0走新广告逻辑
            packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, params, queueVo);
        }
        return firstVideosNewVo;
    }

    /**
     * 根据视频Id查询视频列表
     * @param params
     * @param videoIdList
     * @param data
     * @return
     */
    private List<Videos161Vo> getVideos161VosOther(Map<String, Object> params, List<String> videoIdList, String data) {
        List<Videos161Vo> resultVideos = Lists.newArrayList();
        Map<String, Object> dataMap = JSONObject.parseObject(data, Map.class);
        //精选视频
        Object selectedVideoObject = MapUtils.getObject(dataMap, "selectedVideo");
        List<String> selectedVideoIds = Lists.newArrayList();
        if (selectedVideoObject != null) {
            selectedVideoIds = (List<String>) selectedVideoObject;
            selectSpecialVideos(resultVideos,selectedVideoIds,videoIdList);
        }
        //普通视频
        Object videoObject = MapUtils.getObject(dataMap, "video");
        if (videoObject != null) {
            List<String> videoList = (List<String>)videoObject;
            videoIdList.addAll(videoList);
        }
        //根据视频id查询es
        List<Videos161Vo> esSearchVideos = findRecommendEsService.list(videoIdList, "0");
        if (isNotEmpty(esSearchVideos)) {
            resultVideos.addAll(esSearchVideos);
        }
        String userId = MapUtils.getString(params, "userId");
        String mobileType = MapUtils.getString(params, "mobileType");
        String appVersion = MapUtils.getString(params, "appVersion");
        fillVideoInfo(userId, mobileType, appVersion, resultVideos, selectedVideoObject, selectedVideoIds);
        return resultVideos;
    }

    //按照selectedVideo的Id来排序
    private void setListOrder(List<String> selectedVideoIds, List<Videos161Vo> resultVideos) {
        Collections.sort(resultVideos, ((o1, o2) -> {
            int io1 = selectedVideoIds.indexOf(o1.getId().toString());
            int io2 = selectedVideoIds.indexOf(o2.getId().toString());
            if (io1 != -1) {
                io1 = resultVideos.size() - io1;
            }
            if (io2 != -1) {
                io2 = resultVideos.size() - io2;
            }
            return io2 - io1;
        }));
    }

    /**
     * 组装查询条件
     * @param params
     * @return
     */
    private FirstVideoParamsVo getFirstVideoParamsVo(Map<String, Object> params, String publicInfo) {
        FirstVideoParamsVo paramsVo = new FirstVideoParamsVo();
        String[] ipInfo = ipService.getCurrentIpInfo();
        if (ipInfo != null) {
            paramsVo.setIp(ipInfo[0]);
        }
        String videoId = MapUtils.getString(params, "id");
        if (StringUtils.isNotEmpty(videoId)) {
            paramsVo.setVideoId(videoId);
        }
        paramsVo.setPublicInfo(publicInfo);
        paramsVo.setCatid(params.get("catId").toString());
        paramsVo.setNum("8");
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(params.get("marketChannelId").toString(), params.get("appVersion").toString());
        if (marketAudit != null && marketAudit.getSensitiveState() == 1) {
            paramsVo.setSensitiveState("1");
        } else {
            paramsVo.setSensitiveState("0");
        }
        return paramsVo;
    }

    @Override
    public FirstVideoDetailVo getRecommendDetailVideoList(AbTestAdvParamsVo queueVo, Map<String, Object> params, String publicInfo) {
        boolean errorFlag = false;
        List<String> videoIdList = Lists.newArrayList();
        //是否符合版本
        boolean isVS = !VersionUtil.compareIsHigh(Constant.APPVERSION_280, params.get("appVersion").toString());
        if (isVS && StringUtils.isNotBlank(publicInfo)) {
            FirstVideoParamsVo paramsVo = getFirstVideoParamsVo(params, publicInfo);
            try {
                long pt = System.currentTimeMillis();
                String result = predictService.getRecommendVideoIds(paramsVo, Constant.recommend3);
                log.warn("{} ,大数据详情列表耗时：{},详情列表结果：{}", params.get("deviceId").toString(), (System.currentTimeMillis() - pt), result);
                /*if ((System.currentTimeMillis() - pt) > Constant.TIMEOUT_CODE_MAX) {
                    log.error("{},大数据详情列表耗时：{}", params.get("deviceId").toString(),(System.currentTimeMillis() - pt));
                }*/
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)){
                    String data = jsonObject.getString("data");
                    if (StringUtils.isNotEmpty(data)) {
                        List<Videos161Vo> firstVideos = getVideos161VosOther(params, videoIdList, data);
                        return getFirstDetailVideoResultList(params, queueVo, firstVideos);
                    } else {
                        errorFlag = true;
                    }
                } else {
                    log.error("{},详情列表接口请求大数据接口失败：{}",params.get("deviceId").toString(),result);
                    errorFlag = true;
                }
            } catch (Exception e) {
                log.error("{},详情列表接口请求接口失败：{}",params.get("deviceId").toString(),e.getMessage(),e);
                errorFlag = true;
            }
        }
        if (errorFlag) {
            Object videoCache = VideoCacheUtils.getObject(Constant.videoCacheList3);
            return (FirstVideoDetailVo)videoCache;
        }
        return firstVideosService.firstVideosDetailList25(queueVo, params);
    }

    /**
     * 视频列表和广告列表组装
     * @param params
     * @param queueVo
     * @param videos
     * @return
     */
    private FirstVideoDetailVo getFirstDetailVideoResultList(Map<String, Object> params, AbTestAdvParamsVo queueVo, List<Videos161Vo> videos) {
        FirstVideoDetailVo firstVideosNewVo = new FirstVideoDetailVo();
        firstVideosNewVo.setVideos(videos);
        String videoId = MapUtils.getString(params,"id");
        if (videoId != null && Long.valueOf(videoId) > 0) {
            FirstVideoEsVo firstVideoEsVo = videoEsService.getById(Long.valueOf(videoId));
            if (firstVideoEsVo != null) {
                Videos161Vo videos161Vo = VideoUtils.packaging(firstVideoEsVo);
                String appVersion = MapUtils.getString(params, "appVersion");
                if (videos161Vo.getIncentiveVideo() != null && videos161Vo.getIncentiveVideo() > 0) {
                    String incentiveVideoRate = "0";
                    Map<String, Object> advParam = new HashMap<>();
                    advParam.put("queryType", "position");
                    advParam.put("positionType", "incentiveVideoPosition");
                    advParam.put("mobileType", MapUtils.getString(params, "mobileType"));
                    advParam.put("appPackage", MapUtils.getString(params, "appPackage"));
                    List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(queueVo, advParam, 3);
                    if (CollectionUtils.isNotEmpty(list)) {
                        incentiveVideoRate = list.get(0).getSecondLoadPosition() == null ? "0" : list.get(0).getSecondLoadPosition() + "";
                    }
                    double rate = Double.parseDouble(incentiveVideoRate);
                    videos161Vo.setIncentiveRate(rate);
                }
                boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_322);
                if (newFlag) {
                    String albumId = MapUtils.getString(params, "albumId");
                    if (StringUtils.isNotEmpty(albumId)) {
                        videos161Vo.setAlbumId(Long.valueOf(albumId));
                    }
                    VideoGatherVo videoGatherVo = albumService.getCurrentVideoAlbums(videos161Vo, MapUtils.getString(params, "appPackage"));
                    firstVideosNewVo.setVideoGatherVo(videoGatherVo);
                } else {
                    VideoGatherVo videoGatherVo = videoGatherService.getCurrentVideoGatherVo(videos161Vo);
                    firstVideosNewVo.setVideoGatherVo(videoGatherVo);
                }
            }
        }
        List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, params);
        firstVideosNewVo.setAdvertCodeVos(advertCodeVos);
        return firstVideosNewVo;
    }

    /**
     * 是否使用新算法(使用取模算法，把deviceId进行md5加密后，在转成hashcode，用这个hashcode进行取模，如果取模后值是0，则使用新算法)
     * @return true：使用新算法，false：使用旧算法
     */
    @Override
    public boolean isABTestUser(VideoParamsDto dto, String publicInfo, int type) {
        String uuid = "";
        try {
            PublicInfo pbInfo  = new PublicInfo(publicInfo);
            uuid = pbInfo.getUuid();
        } catch (Exception e) {
            return false;
        }
        String testStr = "user_recommend_video_test";
        String modStr = "user_recommend_video_mod";
        String saltStr = "user_recommend_video_salt";
        if (type == Constant.recommend1) {
            dto.setUuid(uuid);
        } else if (type == Constant.recommend2) {
            testStr = "user_recommend_other_test";
            modStr = "user_recommend_other_mod";
            saltStr = "user_recommend_other_salt";
        } else if (type == Constant.recommend3) {
            testStr = "user_recommend_detail_test";
            modStr = "user_recommend_detail_mod";
            saltStr = "user_recommend_detail_salt";
        }
        //判定是不是指定测试用户
        Object use_recommend_test = Global.getObject(testStr);
        if (use_recommend_test != null && use_recommend_test.toString().contains(uuid)) {
            return true;
        }
        //使用新算法mod值。如果值为0，则都用旧算法；如果值为1，则都使用新算法
        int useRecommendMod = Global.getInt(modStr);
        if (useRecommendMod == 0) {
            return false;
        }
        if (useRecommendMod == 1) {
            return true;
        }
        try {
            String firtsalt = Global.getValue(saltStr);
            String md5 = DigestUtils.md5Hex(uuid.concat(firtsalt)).substring(16, 16 + 8);
            long t = Long.parseLong(md5, 16);
            if (t % useRecommendMod == 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("A/B推荐用户取模异常", e);
        }
        return false;
    }
}
