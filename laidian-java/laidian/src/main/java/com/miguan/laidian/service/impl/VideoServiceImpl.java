package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.entity.UserLabelDefault;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.entity.VideosCat;
import com.miguan.laidian.mapper.*;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.repositories.VideosCatDao;
import com.miguan.laidian.service.UserBuriedPointService;
import com.miguan.laidian.service.UserLabelDefaultService;
import com.miguan.laidian.service.VideoService;
import com.miguan.laidian.service.VideoSettingService;
import com.miguan.laidian.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频源列表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-07-08
 **/
@Slf4j
@Service("videosService")
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoMapper videosMapper;

    @Resource
    private VideoUserMapper videoUserMapper;

    @Resource
    private VideosCatDao videosCatDao;

    @Resource
    private SmallVideoUserMapper clUserVideosMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoLabelMapper videoLabelMapper;

    @Resource
    private VideosChannelRecoMapper videosChannelRecoMapper;

    @Resource
    private VideoSettingService videoSettingService;

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @Resource
    private LdInterestTagMapper tagMapper;

    @Resource
    private UserLabelDefaultService defaultService;

    //视频数量
    private final static int videoNum10 = 10;
    private final static int videoNum20 = 20;
    private final static int videoNum40 = 40;
    private final static int videoNum60 = 60;
    private final static int videoNum100 = 100;
    private final static int videoNum200 = 200;

    @Override
    public List<VideosCat> findAll() {
        return videosCatDao.findVideosCatList(1L);
    }

    @Override
    public Page<Video> findVideosList(Map<String, Object> params, int currentPage, int pageSize) {
        //屏蔽已禁用的分类
        List<VideosCat> videosCatList = videosCatDao.findVideosCatList(-1L);
        if (CollectionUtils.isNotEmpty(videosCatList)) {
            List<Long> excludeCatIds = videosCatList.stream().map(VideosCat::getId).collect(Collectors.toList());
            params.put("excludeCatIds", excludeCatIds);
        }
        //视频是否历史设置过（登录状态才判断）
        String userId = MapUtils.getString(params, "tempUserId");
        params.remove("tempUserId");
        List<Video> videosList = this.getVideosListByTag(params, currentPage, pageSize);
        if (StringUtils.isNotBlank(userId)&&!"0".equals(userId)){
            List<Long> videoIds = videoSettingService.judgeUserIsSet(Long.valueOf(userId));
            if (CollectionUtils.isNotEmpty(videoIds)&&CollectionUtils.isNotEmpty(videosList)){
                for (int i = 0; i < videosList.size(); i++) {
                    Video video = videosList.get(i);
                    if(videoIds.contains(video.getId())){
                        video.setHistoryTab(1);//历史设置过
                    }
                }
            }
        }
        //设置标签信息
        setLabelNameList(videosList);
        //统计视频曝光数
        videoExposureSendToMQ(videosList);
        //设置广告参数
        String appType = MapUtils.getString(params, "appType");
        setAdvConfig(appType, videosList);
        return (Page<Video>) videosList;
    }

    /**
     *  根据条件获取视频
     *
     * @param params
     */
    private List<Video> getVideosListByTag(Map<String, Object> params, int currentPage, int pageSize) {
        String videoType = MapUtils.getString(params, "videoType");
        String videoId = MapUtils.getString(params, "videoId");
        String ABTestFlag = MapUtils.getString(params, "ABTestFlag");
        String appVersion = MapUtils.getString(params, "appVersion");
        boolean isHighFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_278);
        //2.7.9及以后版本是否命中AB实验
        boolean ABTest = false;
        if (isHighFlag && Constant.ABTest.equals(ABTestFlag)) {
            ABTest = true;
        }
        if (ABTest && "10".equals(videoType) && StringUtils.isBlank(videoId)) {
            String deviceId = MapUtils.getString(params, "deviceId");
            String appType = MapUtils.getString(params, "appType");
            //是否新用户
            boolean isNew = userBuriedPointService.getUserState(deviceId, appType);
            //获取用户标签cat_id
            List<LdInterestTagVo> tagVoList = tagMapper.getUserTagList(params);
            //从缓存中取10条视频Id
            List<String> idCacheList = getCacheIdList(deviceId);
            //是否按兴趣标签查询200条视频Id：true-是，false-否
            boolean interestTag = true;
            if (CollectionUtils.isEmpty(tagVoList)) {
                String channelId = MapUtils.getString(params, "channelId");
                UserLabelDefault labelDefault = defaultService.getUserLabelDefault(channelId);
                if (isHighFlag && labelDefault != null && labelDefault.getCatId1() != null && labelDefault.getCatId2() != null) {
                    return getChannelVideoIds(labelDefault, idCacheList, params, currentPage, pageSize);
                } else {
                    if (isNew) {
                        //新用户未设置兴趣标签走老规则
                        return getVideosList(params, currentPage, pageSize);
                    } else {
                        //老用户未设置兴趣标签，不筛选兴趣标签
                        interestTag = false;
                    }
                }
            }
            return getInterestVideoIds(tagVoList, idCacheList, params, interestTag, currentPage, pageSize);
        } else {
            return getVideosList(params, currentPage, pageSize);
        }
    }

    /**
     * 从缓存中取10条视频Id
     * @param deviceId
     * @return
     */
    private List<String> getCacheIdList(String deviceId) {
        List<String> idCacheList = Lists.newArrayList();
        String key = RedisKeyConstant.INTEREST_ID_LIST + deviceId;
        String idCache = redisService.get(key);
        if (StringUtils.isNotEmpty(idCache)) {
            List<String> totalIdList = Arrays.asList(idCache.split(","));
            if (totalIdList.size() < videoNum10) {
                redisService.del(key);
            } else {
                idCacheList = totalIdList.subList(0, videoNum10);
                if (totalIdList.size() > videoNum10) {
                    //剩下的重新放入redis
                    List<String> idCaches = totalIdList.subList(videoNum10, totalIdList.size());
                    String idStr = idCaches.stream().collect(Collectors.joining(","));
                    Integer seconds = redisService.ttl(key) == null ? 20 : redisService.ttl(key).intValue();
                    redisService.set(key, idStr, seconds);
                } else if (totalIdList.size() == videoNum10) {
                    redisService.del(key);
                }
            }
        }
        return idCacheList;
    }

    /**
     * 根据感兴趣标签获取视频Id
     * @param tagVoList
     * @param idCacheList
     * @param params
     * @param interestTag
     * @param currentPage
     * @param pageSize
     * @return
     */
    private List<Video> getInterestVideoIds(List<LdInterestTagVo> tagVoList, List<String> idCacheList, Map<String, Object> params, boolean interestTag, int currentPage, int pageSize) {
        List<Video> resultList;
        String deviceId = MapUtils.getString(params, "deviceId");
        if (CollectionUtils.isNotEmpty(idCacheList) && idCacheList.size() >= videoNum10) {
            String tagIds = idCacheList.stream().collect(Collectors.joining(","));
            resultList = getVideosById(params, tagIds);
        } else {
            Map<String, Object> queryIdsMap = new HashMap<>();
            queryIdsMap.put("state", Constant.open);
            queryIdsMap.put("approvalState", Constant.approvalState);
            queryIdsMap.put("recommend", Video.RECOMMEND);
            queryIdsMap.put("number", videoNum200);
            String interestCatIds = "";
            if (interestTag) {
                interestCatIds = tagVoList.stream().map(t -> t.getCatId() + "").collect(Collectors.joining(","));
                queryIdsMap.put("interestCatIds", interestCatIds);
            }
            List<Long> idList = videosMapper.findVideosIdList(queryIdsMap);
            if (CollectionUtils.isEmpty(idList) || idList.size() < videoNum10) {
                log.error("首页热门视频总数小于10条，设备号：{},感兴趣标签：{}", deviceId, interestCatIds);
                return getVideosList(params, currentPage, pageSize);
            } else {
                //截取10条视频Id数据
                String tagIds = getTagIds(idList, deviceId, interestCatIds);
                resultList = getVideosById(params, tagIds);
            }
        }
        Page<Video> page = getVideosPage(currentPage, pageSize, resultList, deviceId);
        return page;
    }

    /**
     * 根据渠道标签获取视频Id
     * 200条视频占比：第一标签50%，第二标签30%，其他标签20%
     * @param labelDefault
     * @param idCacheList
     * @param params
     * @param currentPage
     * @param pageSize
     * @return
     */
    private List<Video> getChannelVideoIds(UserLabelDefault labelDefault, List<String> idCacheList, Map<String, Object> params, int currentPage, int pageSize) {
        List<Video> resultList;
        String deviceId = MapUtils.getString(params, "deviceId");
        if (CollectionUtils.isNotEmpty(idCacheList) && idCacheList.size() >= videoNum10) {
            String tagIds = idCacheList.stream().collect(Collectors.joining(","));
            resultList = getVideosById(params, tagIds);
        } else {
            List<Long> idList = getVideoIds(labelDefault);
            if (CollectionUtils.isEmpty(idList) || idList.size() < videoNum10) {
                log.error("首页热门视频总数小于10条，设备号：{},感兴趣标签：{}", deviceId, "");
                return getVideosList(params, currentPage, pageSize);
            } else {
                //截取10条视频Id数据
                String tagIds = getTagIds(idList, deviceId, "");
                resultList = getVideosById(params, tagIds);
            }
        }
        Page<Video> page = getVideosPage(currentPage, pageSize, resultList, deviceId);
        return page;
    }

    //根据渠道标签获取200条视频Id
    private List<Long> getVideoIds(UserLabelDefault labelDefault) {
        Map<String, Object> queryIdsMap = new HashMap<>();
        queryIdsMap.put("state", Constant.open);
        queryIdsMap.put("approvalState", Constant.approvalState);
        queryIdsMap.put("recommend", Video.RECOMMEND);
        List<Long> idList = Lists.newArrayList();
        int catIdNum1 = videoNum100;
        int catIdNum2 = videoNum60;
        int catIdNum3 = videoNum40;
        queryIdsMap.put("number", catIdNum1);
        queryIdsMap.put("interestCatIds", labelDefault.getCatId1());
        List<Long> idList1 = videosMapper.findVideosIdList(queryIdsMap);
        idList.addAll(idList1);
        //第一标签不足时，第二标签补足
        catIdNum2 = catIdNum2 + catIdNum1 - idList1.size();
        queryIdsMap.put("number", catIdNum2);
        queryIdsMap.put("interestCatIds", labelDefault.getCatId2());
        List<Long> idList2 = videosMapper.findVideosIdList(queryIdsMap);
        idList.addAll(idList2);
        //第二标签不足时，其他标签补足
        catIdNum3 = catIdNum3 + catIdNum2 - idList2.size();
        queryIdsMap.put("number", catIdNum3);
        queryIdsMap.remove("interestCatIds");
        String exceptCatIds = labelDefault.getCatId1() + "," + labelDefault.getCatId2();
        queryIdsMap.put("exceptCatIds", exceptCatIds);
        List<Long> idList3 = videosMapper.findVideosIdList(queryIdsMap);
        idList.addAll(idList3);
        Collections.shuffle(idList);
        return idList;
    }

    /**
     * 组装视频分页信息
     * @param currentPage
     * @param pageSize
     * @param resultList
     * @param deviceId
     * @return
     */
    private Page<Video> getVideosPage(int currentPage, int pageSize, List<Video> resultList, String deviceId) {
        Page<Video> page = new Page<>();
        int totalNum = videoNum200;
        String keyCount = RedisKeyConstant.VIDEO_ID_COUNT + deviceId;
        String videoIdCnt = redisService.get(keyCount);
        if (StringUtils.isNotEmpty(videoIdCnt)) {
            totalNum = Integer.valueOf(videoIdCnt);
        }
        int pages = totalNum%pageSize == 0 ? totalNum/pageSize : totalNum/pageSize +1;
        page.setTotal(totalNum);
        page.setPages(pages);
        page.setPageNum(currentPage);
        page.setPageSize(pageSize);
        page.getResult().addAll(resultList);
        return page;
    }

    /**
     * 截取10条视频Id数据，大于10的部分存放redis
     * @param idList
     * @param deviceId
     * @return
     */
    private String getTagIds(List<Long> idList, String deviceId, String interestCatIds) {
        List<Long> ids = idList.subList(0, videoNum10);
        if (idList.size() > videoNum20) {
            //存放redis
            List<Long> idCaches = idList.subList(videoNum10, idList.size());
            String idStr = idCaches.stream().map(d -> d.longValue() + "").collect(Collectors.joining(","));
            Map<String, String> idMap = new HashMap<>();
            idMap.put("ids", idStr);
            idMap.put("deviceId", deviceId);
            idMap.put("interestCatIds", interestCatIds);
            String dataStr = JSON.toJSONString(idMap);
            rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_INTEREST_ID_EXCHANGE, RabbitMQConstant.VIDEO_INTEREST_ID_KEY, dataStr);
        }
        return ids.stream().map(d -> d.longValue() + "").collect(Collectors.joining(","));
    }

    /**
     * 根据视频Id查询推荐内容
     * @param params
     * @param tagIds
     * @return
     */
    private List<Video> getVideosById(Map<String, Object> params, String tagIds) {
        params.put("tagIds", tagIds);
        return videosMapper.findVideosListById(params);
    }

    @Override
    public void saveInterestVideoIds(String ids, String deviceId, String interestCatIds) {
        String key = RedisKeyConstant.INTEREST_ID_LIST + deviceId;
        redisService.set(key, ids, RedisKeyConstant.INTEREST_ID_LIST_SECONDS);
        //查询视频总数缓存到redis
        Map<String, Object> queryIdsMap = new HashMap<>();
        queryIdsMap.put("state", Constant.open);
        queryIdsMap.put("approvalState", Constant.approvalState);
        queryIdsMap.put("recommend", Video.RECOMMEND);
        if (StringUtils.isNotEmpty(interestCatIds)) {
            queryIdsMap.put("interestCatIds", interestCatIds);
        }
        Integer cntNum = videosMapper.findVideosIdCount(queryIdsMap);
        String keyCount = RedisKeyConstant.VIDEO_ID_COUNT + deviceId;
        redisService.set(keyCount, cntNum, RedisKeyConstant.INTEREST_ID_LIST_SECONDS);
    }

    /**
     *  根据条件获取视频
     *
     * @param params
     */
    private List<Video> getVideosList(Map<String, Object> params, int currentPage, int pageSize) {
        String videoType = MapUtils.getString(params, "videoType");
        if ("10".equals(videoType)) {
            String channelId = MapUtils.getString(params, "channelId");
            VideosChanelRecoVo videosChanelRecoVo = videosChannelRecoMapper.queryVideosChannelRecoInfo(channelId);
            List<String> idsList = new ArrayList<>();
            if (videosChanelRecoVo != null) {
                String recoJson = videosChanelRecoVo.getRecoJson();
                List<RecoJsonVo> list = JSONObject.parseArray(recoJson, RecoJsonVo.class);
                if (CollectionUtils.isNotEmpty(list)){
                    list.sort(Comparator.comparing(RecoJsonVo::getSort));
                    for (RecoJsonVo recoJsonVo : list) {
                        String videoIds = recoJsonVo.getVideoIds();
                        String[] split = videoIds.split(",");
                        List<String> asList = Arrays.asList(split);
                        for (String str : asList) {
                            if (!idsList.contains(str)) {
                                idsList.add(str);
                            }
                        }
                    }
                }
            }
            params.put("ids", StringUtils.join(idsList.toArray(),","));
            //根据推荐渠道获取视频
            List<Video> list = new ArrayList<>();
            if(idsList.size()>0){
                fillParam(params, currentPage, pageSize, idsList);
                Long total = videosMapper.countVideosListByChannelDoublePage(params);
                list = videosMapper.findVideosListByChannelDoublePage(params);
                Page<Video> page = new Page<>();
                page.setTotal(total);
                Long pages = total%pageSize == 0 ? total/pageSize : total/pageSize +1;
                page.setPages(pages.intValue());
                page.setPageNum(currentPage);
                page.setPageSize(pageSize);
                if (CollectionUtils.isNotEmpty(list)){
                    Map<Long, List<Video>> listMap = list.stream().collect(Collectors.groupingBy(t -> t.getId()));
                    if (currentPage == 1){
                        for (int i = 0; i < idsList.size(); i++) {
                            Long id = Long.valueOf(idsList.get(i));
                            Video video = listMap.get(id).get(0);
                            list.set(i,video);
                        }
                    }
                }
                page.getResult().addAll(list);
                return page;
            } else {
                PageHelper.startPage(currentPage, pageSize);
                list = videosMapper.findVideosListByChannel(params);
            }
            if (CollectionUtils.isNotEmpty(list)){
                Map<Long, List<Video>> listMap = list.stream().collect(Collectors.groupingBy(t -> t.getId()));
                if (currentPage == 1){
                    for (int i = 0; i < idsList.size(); i++) {
                        Long id = Long.valueOf(idsList.get(i));
                        Video video = listMap.get(id).get(0);
                        list.set(i,video);
                    }
                }
            }
            return list;
        }
        //查询视频信息
        PageHelper.startPage(currentPage, pageSize);
        return videosMapper.findVideosList(params);
    }

    private void fillParam(Map<String, Object> params, int currentPage, int pageSize, List<String> idsList) {
        //分页处理
        //渠道视频如果比
        int recoPage = 0;
        int recoPageSize = pageSize;
        int wavedioPage = 0;
        int wavedioPageSize = pageSize;
        //比方说size = 48  ;  currentPage = 4 , pageSize = 10 . 全走ids,  当前页为5时，8条ids , 2条推荐
        int start = (currentPage - 1) * pageSize; //起始位置.
        int end = currentPage * pageSize; //起始位置.
        if(idsList.size() > end){
            recoPage = start;
            recoPageSize = pageSize;
            wavedioPage = 0;
            wavedioPageSize = 0;
        } else if(idsList.size() > start && idsList.size()<=end){  //cu
            recoPage = (currentPage - 1) * pageSize;  //40
            recoPageSize = idsList.size() - start;  //8
            wavedioPage = 0;   //第0页开始
            wavedioPageSize = end - idsList.size(); //2
        } else {
           //逻辑  idsList.size() <= start
            //全拿wavedio
            recoPage = 0;
            recoPageSize = 0;
            //起始位置
            wavedioPage = start - idsList.size();  // 50-48 = 2
            wavedioPageSize = pageSize; //10
        }
        params.put("recoPage",recoPage);
        params.put("recoPageSize",recoPageSize);
        params.put("wavedioPage",wavedioPage);
        params.put("wavedioPageSize",wavedioPageSize);
    }

    //设置标签信息
    private void setLabelNameList(List<Video> videosList) {
        if (CollectionUtils.isEmpty(videosList)) {
            return;
        }
        Random random = new Random();
        for (int i = 0; i < videosList.size(); i++) {
            Video video = videosList.get(i);
            String labelName = video.getLabelName();
            if (StringUtils.isNotEmpty(labelName)) {
                String[] split = labelName.split(",");
                List<String> strList = new ArrayList(Arrays.asList(split));
                int size = strList.size();
                if (size > 3) {
                    List<String> list = new ArrayList<>();
                    while (list.size() < 3) {
                        String str = strList.get(random.nextInt(size));
                        if (!list.contains(str)) {
                            list.add(str);
                        }
                    }
                    video.setLabelNameList(list);
                } else {
                    video.setLabelNameList(strList);
                }
            }
        }
    }

    /**
     * 设置广告相关配置信息
     *
     * @param appType
     * @param videosList
     */
    @Override
    public void setAdvConfig(String appType, List<Video> videosList) {
        int callListCount = Global.getInt("call_list_count", appType);//android来电列表广告位次数
        int callDetailsCount = Global.getInt("call_details_count", appType);//android来电详情广告位次数
        int callDetailsDeblockingCount = Global.getInt("call_details_deblocking_count", appType);//android来电详情解锁广告位次数
        int callDetailsQuitCount = Global.getInt("call_details_quit_count", appType);//android来电详情退出广告位次数
        int callDetailsQuitLimit = Global.getInt("call_details_quit_limit", appType);//android来电详情退出广告位每天上限
        int lockScreeneblockingCount = Global.getInt("lock_screen_deblocking", appType);//android锁屏广告位次数
        int laidianUnlockDays = Global.getInt("laidian_unlock_days", appType);//android来电分类解锁广告的天数配置
        int laidianForceDays = Global.getInt("laidian_force_days", appType);//android来电强制广告的天数配置
        int closePopupTodayTotalCount = Global.getInt("close_popup_today_total_count", appType);//关闭弹窗广告位当日总次数
        for (Video videosVo : videosList) {
            videosVo.setCallListCount(callListCount);
            videosVo.setCallDetailsCount(callDetailsCount);
            videosVo.setCallDetailsDeblockingCount(callDetailsDeblockingCount);
            videosVo.setCallDetailsQuitCount(callDetailsQuitCount);
            videosVo.setCallDetailsQuitLimit(callDetailsQuitLimit);
            videosVo.setLockScreeneblockingCount(lockScreeneblockingCount);
            videosVo.setLaidianUnlockDays(laidianUnlockDays);
            videosVo.setLaidianForceDays(laidianForceDays);
            videosVo.setClosePopupTodayTotalCount(closePopupTodayTotalCount);
        }
    }

    @Override
    @Transactional
    public int emptyMyCollection(String userId) {
        //批量更新收藏状态
        clUserVideosMapper.emptyMyCollectionByUserId(userId);
        //查询取消收藏的信息列表
        List<SmallVideoUserVo> collectionsList = clUserVideosMapper.findCollectionsListByUserId(userId);
        if (collectionsList.isEmpty()) {
            return 0;
        }
        Long[] smallStr = collectionsList.stream().collect(Collectors.groupingBy(SmallVideoUserVo::getVideoId)).keySet().toArray(new Long[collectionsList.size()]);
        return clUserVideosMapper.batchUpdateSmallvideos(smallStr);
    }

    @Override
    public Video findOne(Map<String, Object> params) {
        String userId = MapUtils.getString(params, "tempUserId");
        params.remove("tempUserId");
        String appType = MapUtils.getString(params, "appType");
        Video videosVo = videosMapper.findOne(params);
        if (videosVo != null) {
            int callListCount = Global.getInt("call_list_count", appType);//android来电列表广告位次数
            int callDetailsCount = Global.getInt("call_details_count", appType);//android来电详情广告位次数
            int callDetailsDeblockingCount = Global.getInt("call_details_deblocking_count", appType);//android来电详情解锁广告位次数
            int callDetailsQuitCount = Global.getInt("call_details_quit_count", appType);//android来电详情退出广告位次数
            int callDetailsQuitLimit = Global.getInt("call_details_quit_limit", appType);//android来电详情退出广告位每天上限
            int lockScreeneblockingCount = Global.getInt("lock_screen_deblocking", appType);//android锁屏广告位次数
            int laidianUnlockDays = Global.getInt("laidian_unlock_days", appType);//android来电分类解锁广告的天数配置
            int laidianForceDays = Global.getInt("laidian_force_days", appType);//android来电强制广告的天数配置
            videosVo.setCallListCount(callListCount);
            videosVo.setCallDetailsCount(callDetailsCount);
            videosVo.setCallDetailsDeblockingCount(callDetailsDeblockingCount);
            videosVo.setCallDetailsQuitCount(callDetailsQuitCount);
            videosVo.setCallDetailsQuitLimit(callDetailsQuitLimit);
            videosVo.setLockScreeneblockingCount(lockScreeneblockingCount);
            videosVo.setLaidianUnlockDays(laidianUnlockDays);
            videosVo.setLaidianForceDays(laidianForceDays);
            //设置标签信息
            String labelName = videosVo.getLabelName();
            if (StringUtils.isNotEmpty(labelName)) {
                String[] split = labelName.split(",");
                List<String> list = Arrays.asList(split);
                videosVo.setLabelNameList(list);
            }
            //视频是否历史设置过（登录状态才判断）
            if (StringUtils.isNotBlank(userId)&&!"0".equals(userId)){
                List<Long> videoIds = videoSettingService.judgeUserIsSet(Long.valueOf(userId));
                if (CollectionUtils.isNotEmpty(videoIds)){
                        if(videoIds.contains(videosVo.getId())){
                            videosVo.setHistoryTab(1);//历史设置过
                        }
                }
            }
        }
        return videosVo;
    }

    @Override
    public boolean updateCountSendMQ(Map<String, Object> params) {
        String json = JSON.toJSONString(params);
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_UPDATECOUNT_EXCHANGE, RabbitMQConstant.VIDEO_UPDATECOUNT_KEY, json);
        return true;
    }

    @Transient
    @Override
    public boolean updateCount(Map<String, Object> params) {
        try {
            //1.5需要增加关系表维护每个马甲包的数据 add shixh 0929
            videosMapper.updateCount(params);//更新主表
            String appType = MapUtils.getString(params, "appType");
            if (StringUtils.isNotEmpty(appType)) {
                String opType = MapUtils.getString(params, "opType");
                Long videoId = MapUtils.getLong(params, "videoId");
                VideoUserVo videoUserVo = videoUserMapper.findOne(params);
                if (videoUserVo == null && videoId != null && videoId > 0) {
                    //操作类型：10--收藏 20--分享 30--取消收藏 40--点击 50--来电设置成功数"
                    videoUserVo = new VideoUserVo();
                    videoUserVo.setAppType(appType);
                    videoUserVo.setClickCount("40".equals(opType) ? "1" : "0");
                    videoUserVo.setVideoId(videoId);
                    videoUserVo.setShareCount("20".equals(opType) ? "1" : "0");
                    videoUserVo.setSuccessNum("50".equals(opType) ? "1" : "0");
                    videoUserVo.setUserLikeCount("10".equals(opType) ? "1" : "0");//30不处理
                    videoUserMapper.save(videoUserVo);
                } else {
                    videoUserMapper.updateCount(params);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public int updateType(Map<String, Object> params) {
        return videosMapper.updateType(params);
    }

    @Override
    public Page<Map<String, Object>> findCollectionList(Map<String, Object> params, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        return (Page<Map<String, Object>>) videosMapper.findCollectionList(params);
    }

    /**
     * 获得分享视频列表(分享视频，以及热门前8的视频)
     *
     * @param videoId 分享视频的id
     * @return
     */
    @Override
    public List<Map<String, Object>> gainShareVideos(String videoId) {
        return videosMapper.gainShareVideos(videoId);
    }

    @Override
    public int batchDelCollections(String[] collectionIds) {
        //批量更新收藏状态
        clUserVideosMapper.batchDelCollections(collectionIds);
        //查询取消收藏的信息列表
        List<SmallVideoUserVo> collectionsList = clUserVideosMapper.findCollectionsList(collectionIds);
        if (collectionsList.isEmpty()) {
            return 0;
        }
        Long[] smallStr = collectionsList.stream().collect(Collectors.groupingBy(SmallVideoUserVo::getVideoId)).keySet().toArray(new Long[collectionsList.size()]);
        clUserVideosMapper.batchUpdateSmallvideos(smallStr);
        return 1;
    }

    /**
     * 保存上传视频信息
     *
     * @param videosVo
     * @return
     */
    @Override
    public int saveUploadVideos(Video videosVo) {
        //初始化数据
        videosVo.setClickCount(0L);
        videosVo.setOriginalLikeCount("0");
        videosVo.setLikeCount("0");
        videosVo.setShareCount(0L);
        videosVo.setSuccessNum("0");
        videosVo.setApprovalState("0");//默认待审核
        videosVo.setState(02L);//状态 1开启 2关闭
//        videosVo.setBsyImgUrl("http://oss.ss.bscstorage.com/p/dev-img-laidian/15651499826503.gif");//默认图片
        return videosMapper.saveUploadVideos(videosVo);
    }

    /**
     * 批量删除上传视频信息
     *
     * @param uploadVideosIds
     * @return
     */
    @Override
    public int batchDelUploadVideos(String[] uploadVideosIds) {
        return videosMapper.batchDelUploadVideos(uploadVideosIds);
    }

    @Override
    @Cacheable(value = CacheConstant.QUICKSETUPCALL_SHOWVIDEOS, unless = "#result == null")
    public List<VideoVo> quickSetupCallShowVideos(Map<String, Object> params, int num) {
        params.put("queryNumber", num);
        List<Video> videos = videosMapper.findVideosList(params);
        List<VideoVo> videoVos = videos.stream().map(video -> {
            VideoVo videoVo = new VideoVo();
            BeanUtils.copyProperties(video, videoVo);
            return videoVo;
        }).collect(Collectors.toList());
        //视频是否历史设置过（登录状态才判断）
        String userId = MapUtils.getString(params, "tempUserId");
        if (StringUtils.isNotBlank(userId)&&!"0".equals(userId)){
            List<Long> videoIds = videoSettingService.judgeUserIsSet(Long.valueOf(userId));
            if (CollectionUtils.isNotEmpty(videoIds)&&CollectionUtils.isNotEmpty(videoVos)){
                for (int i = 0; i < videoVos.size(); i++) {
                    VideoVo videoVo = videoVos.get(i);
                    if(videoIds.contains(videoVo.getId())){
                        videoVo.setHistoryTab(1);//历史设置过
                    }
                }
            }
        }
        return videoVos;
    }

    @Override
    public Video findVideo(Map<String, Object> params) {
        return videosMapper.findOne(params);
    }


    /**
     * 查询审核通过最新视频的创建时间
     *
     * @return
     */
    @Override
    public Long queryNewVideosTime() {
        Date newVideoTime = videosMapper.queryNewVideosTime();
        if (newVideoTime == null) {
            return null;
        }
        return newVideoTime.getTime();
    }

    /**
     * 待统计视频曝光数放入MQ
     *
     * @param videoList
     */
    @Override
    public void videoExposureSendToMQ(List<Video> videoList) {
        String json = JSONObject.toJSONString(videoList);
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_EXPOSURE_EXCHANGE, RabbitMQConstant.VIDEO_EXPOSURE_KEY, json);
    }

    /**
     * 统计视频曝光数量:存放redis
     *
     * @param videoList
     */
    @Override
    public void addVideoExposure(List<Video> videoList) {
        if (CollectionUtils.isNotEmpty(videoList) && videoList.get(0) != null) {
            String today = DateUtil.parseDateToStr(new Date(), "yyyyMMdd");
            Map<Long, Long> mapResult = videoList.stream().collect(Collectors.groupingBy(Video::getCatId, Collectors.counting()));
            for (Map.Entry<Long, Long> map : mapResult.entrySet()) {
                String catId = map.getKey().toString();
                Long num = map.getValue();
                String key = RedisKeyConstant.VIDEO_EXPOSURE + today;
                String videoCnt = redisService.hget(key, catId);
                if (StringUtils.isNotEmpty(videoCnt)) {
                    videoCnt = (Long.valueOf(videoCnt) + num) + "";
                    redisService.hset(key, catId, videoCnt, RedisKeyConstant.VIDEO_EXPOSURE_SECONDS);
                } else {
                    redisService.hset(key, catId, num.toString(), RedisKeyConstant.VIDEO_EXPOSURE_SECONDS);
                }
            }
        }
    }

    /**
     * 获取视频曝光统计数
     *
     * @param dates 日期
     * @return
     */
    @Override
    public Map<String, List<VideoExposureVo>> getVideoExposureCountInfo(String dates) {
        Map<String, List<VideoExposureVo>> resultMap = new HashMap<>();
        List<VideosCat> videosCats = videosCatDao.findListByTypeOrderBySortAsc(1L);
        VideosCat videosCat = new VideosCat();
        videosCat.setId(0L);
        videosCats.add(videosCat);
        if (StringUtils.isNotEmpty(dates)) {
            String[] dataStr = dates.split(",");
            for (int i = 0; i < dataStr.length; i++) {
                getCountInfo(resultMap, videosCats, dataStr[i]);
            }
        } else {
            Set<String> catKeys = redisService.keys(RedisKeyConstant.VIDEO_EXPOSURE + "2020*");
            for (String redisKey : catKeys) {
                String day = redisKey.split(":")[2];
                getCountInfo(resultMap, videosCats, day);
            }
        }
        return resultMap;
    }

    /**
     * 按日期循环取曝光数
     *
     * @param resultMap  返回结果
     * @param videosCats 全部视频分类
     * @param day        日期
     */
    private void getCountInfo(Map<String, List<VideoExposureVo>> resultMap, List<VideosCat> videosCats, String day) {
        List<VideoExposureVo> videoExposureVoList = Lists.newArrayList();
        String redisKey = RedisKeyConstant.VIDEO_EXPOSURE + day;
        for (VideosCat vCat : videosCats) {
            String count = redisService.hget(redisKey, vCat.getId().toString());
            if (StringUtils.isNotEmpty(count)) {
                VideoExposureVo videoExposureVo = new VideoExposureVo();
                videoExposureVo.setCatId(vCat.getId().toString());
                videoExposureVo.setCount(count);
                videoExposureVoList.add(videoExposureVo);
            }
        }
        if (CollectionUtils.isNotEmpty(videoExposureVoList)) {
            resultMap.put(day, videoExposureVoList);
        }
    }

    @Override
    public List<VideoLabelVo> topSearchLabel() {
        return videoLabelMapper.topSearchLabel();
    }

    @Override
    public Page<Video> findLabelVideosList(String likeLabelName, CommonParamsVo commomParams) {
        if (StringUtils.isNotEmpty(likeLabelName) && !",".equals(likeLabelName)) {
            Map<String, Object> params = new HashMap<>();
            params.put("likeLabelName", likeLabelName);
            PageHelper.startPage(commomParams.getCurrentPage(), commomParams.getPageSize());
            List<Video> labelVideosList = videoLabelMapper.findLabelVideosList(params);
            //视频是否历史设置过（登录状态才判断）
            String userId = commomParams.getUserId();
            if (StringUtils.isNotBlank(userId)&&!"0".equals(userId)){
                List<Long> videoIds = videoSettingService.judgeUserIsSet(Long.valueOf(userId));
                if (CollectionUtils.isNotEmpty(videoIds)&&CollectionUtils.isNotEmpty(labelVideosList)){
                    for (int i = 0; i < labelVideosList.size(); i++) {
                        Video video = labelVideosList.get(i);
                        if(videoIds.contains(video.getId())){
                            video.setHistoryTab(1);//历史设置过
                        }
                    }
                }
            }
            //设置标签list
            setLabelNameList(labelVideosList);
            //统计视频曝光数
            videoExposureSendToMQ(labelVideosList);
            return (Page<Video>) labelVideosList;
        }
        return new Page<>();
    }
}