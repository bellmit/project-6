package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.file.AWSUtil4Video;
import com.miguan.ballvideo.common.util.video.VideoSQLUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.FirstVideosDeleteDto;
import com.miguan.ballvideo.dto.FirstVideosDto;
import com.miguan.ballvideo.dto.UserCenterDto;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.service.recommend.FindRecommendCatidServiceImpl;
import com.miguan.ballvideo.service.recommend.FindRecommendEsServiceImpl;
import com.miguan.ballvideo.vo.*;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.video.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * 首页视频源列表ServiceImpl
 * 1.6.1新接口放这里迁移
 */
@Service("firstVideosService")
@Slf4j
public class FirstVideosServiceImplAsyn implements FirstVideosService {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private VideoCacheService videoCacheService;

    @Resource(name = "redisDB8Service")
    private RedisDB8Service redisDB8Service;

    @Resource
    private VideosCatMapper videosCatMapper;

    @Resource
    private UserLabelService userLabelService;

    @Resource
    private VideoGatherService videoGatherService;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private VideoEsService videoEsService;

    @Resource
    private AdvertService advertService;

    @Resource
    private ClUserMapper clUserMapper;

    @Resource
    private ClUserService clUserService;

    @Resource
    private FindVideosService findVideosService;

    @Resource
    private FindJLVideosService findJLVideosService;

    @Resource
    private FindRecommendCatidServiceImpl findRecommendCatidService;

    @Resource
    private PredictService predictService;

    @Resource
    private VideoAlbumService albumService;
    @Resource
    private VideoAlbumMapper videoAlbumMapper;
    @Resource
    private FindRecommendEsServiceImpl findRecommendEsService;

    ExecutorService executor = new ThreadPoolExecutor(32, 100, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    private List<Videos161Vo> getRecommendVideos(Map<String, Object> params, Long catId1, Long catId2) {
        final List<Videos161Vo> firstVideosAll = new ArrayList<Videos161Vo>();
        List<Videos161Vo> firstVideosList1;
        List<Videos161Vo> firstVideosList2;
        List<Videos161Vo> firstVideosList3;
        final String userId = MapUtils.getString(params, "userId");
        //获取第一标签随机视频
        final int firstLableValue = Global.getInt("first_label_value");//用户第一标签条数
        params.put("catId", catId1);
        if (userId == null || userId.equals("0")) {
            firstVideosList1 = videoCacheService.getFirstVideos161(params, firstLableValue);
        } else {
            params.put("queryNumber", firstLableValue);
            firstVideosList1 = firstVideosMapper.findFirstVideosListByUserId161(params);
        }
        firstVideosAll.addAll(firstVideosList1);

        //获取第二标签随机视频
        final int secondLableValue = Global.getInt("second_label_value");//用户第二标签条数
        params.remove("queryNumber");
        params.put("catId", catId2);
        if (userId == null || userId.equals("0")) {
            firstVideosList2 = videoCacheService.getFirstVideos161(params, secondLableValue);
        } else {
            params.put("queryNumber", secondLableValue);
            firstVideosList2 = firstVideosMapper.findFirstVideosListByUserId161(params);
        }
        firstVideosAll.addAll(firstVideosList2);

        //获取其他类型随机视频
        final int otherLableValue = Global.getInt("other_label_value");//用户第三标签条数
        params.remove("queryNumber");
        params.remove("catId");
        final List<Long> list = new ArrayList<>();
        list.add(catId1);
        list.add(catId2);

        //1.华为魅族过滤channelId  不展示美女，生活 2.ios不展示美女
        final String marketChannelId = MapUtils.getString(params, "marketChannelId");
        String videosMarketScreen = Global.getValue("videos_market_screen");
        String[] split = videosMarketScreen.split(",");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(marketChannelId)) {
                list.add(Long.valueOf("3"));
                list.add(Long.valueOf("251"));
                break;
            }
        }
        params.put("otherCatIds", list);

        if (userId == null || userId.equals("0")) {
            firstVideosList3 = videoCacheService.getFirstVideos161(params, otherLableValue);
        } else {
            params.put("queryNumber", otherLableValue);
            firstVideosList3 = firstVideosMapper.findFirstVideosListByUserId161(params);
        }
        firstVideosAll.addAll(firstVideosList3);
        return firstVideosAll;
    }

    @Override
    public FirstVideos161Vo firstVideosList161(AbTestAdvParamsVo queueVo, Map<String, Object> params) {
        int state = Global.getInt("use_EsSearch_state2");//Global.getInt("use_EsSearch_state");//0-使用DB查询，1-使用ES查询
        final FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        List<Videos161Vo> firstVideos;
        final int pageNumber = Global.getInt("page_number");
        final String userId = MapUtils.getString(params, "userId");
        //V2.5.6 根据市场审核开关,屏蔽合集
        String appVersion = MapUtils.getString(params, "appVersion");
        String channelId = MapUtils.getString(params, "channelId");
        String isCombine = MapUtils.getString(params, "isCombine");
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
        if (marketAudit != null && StringUtils.isNotBlank(marketAudit.getGatherIds())) {
            params.put("gatherIds", marketAudit.getGatherIds());
        }
        String appPackage = MapUtils.getString(params, "appPackage");
        //屏蔽不生效分类
        List<String> catIds = videosCatMapper.queryCatIdsList(appPackage);
        if (isNotEmpty(catIds)) {
            params.put("otherCatIds", catIds);
        }
        firstVideos = getVideos161Vos(params, state, pageNumber, userId, catIds);
        if (isNotEmpty(firstVideos)) {
            videoCacheService.fillParams(firstVideos);
            //V2.1.0
            //如果大于2.5版本，则不返回集合其余信息
            if (VersionUtil.compareIsHigh(Constant.APPVERSION_250, params.get("appVersion").toString())) {
                //2.1.0新增视频合集展示
                fillGatherVideos(firstVideos);
            }
            //视频作者头像和名字取用户表（虚拟用户或者真是用户）
            clUserService.packagingUserAndVideos(firstVideos);
            String mobileType = MapUtils.getString(params, "mobileType");
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption1(firstVideos, videoAlbumVos, mobileType, appVersion);
        }

        //是否返回广告
        if (VersionUtil.isBetween(Constant.APPVERSION_253, Constant.APPVERSION_257, params.get("appVersion").toString()) && Constant.ANDROID.equals(params.get("mobileType").toString())) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else if("2".equals(isCombine)){
            //双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else {
            params.remove("state");
            boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
            if (newFlag) {
                //V2.5.0走新广告逻辑
                packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, params, queueVo);
            } else {
                List<AdvertVo> advertList = advertOldService.getAdvertsBySection(params);
                VideoUtils.packagAdvertAndVideos(firstVideosNewVo, firstVideos, advertList, appVersion);
            }
        }
        return firstVideosNewVo;
    }

    //查询分类视频数据
    private List<Videos161Vo> getVideos161Vos(Map<String, Object> params, int state, int pageNumber, String userId, List<String> catIds) {
        List<Videos161Vo> firstVideos;//未登录用户使用ES查询 add shixh 0331
        if (userId == null || userId.equals("0")) {
            if (state == 1) {
                log.info("use es search");
                if (isNotEmpty(catIds)) {
                    params.put("otherCatIds", String.join(",", catIds));
                }
                firstVideos = videoEsService.query(params, pageNumber);
            } else {
                firstVideos = videoCacheService.getFirstVideos161(params, pageNumber);
            }
        } else {
            params.put("queryNumber", pageNumber);
            firstVideos = firstVideosMapper.findFirstVideosListByUserId161(params);
        }
        return firstVideos;
    }

    @Override
    public FirstVideos161Vo firstRecommendVideosList161(Map<String, Object> params) {
        final FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        final String deviceId = MapUtils.getString(params, "deviceId");
        //通过设备ID查询标签信息

        final UserLabel userLabel = userLabelService.getUserLabelByDeviceId(deviceId);
        //获取第一第二标签信息
        final Long catId1 = userLabel == null ? 0L : userLabel.getCatId1();
        final Long catId2 = userLabel == null ? 0L : userLabel.getCatId2();
        //首页推荐根据标签获取随机视频
        final List<Videos161Vo> firstVideos = getRecommendVideos(params, catId1, catId2);
        //首页视频计算点赞和观看总数
        if (isNotEmpty(firstVideos)) {
            videoCacheService.fillParams(firstVideos);
        }
        //随机2个广告
        params.remove("queryNumber");//移除随机视频的参数，避免影响广告查询
        final List<AdvertVo> advertVos = advertOldService.queryByRandom(params, 2);
        final List<FirstVideosVo> firstVideosVos = VideoUtils.packaging(advertVos, firstVideos);
        firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        return firstVideosNewVo;
    }

    public String[] getDuty(UserLabelVo userLabelVo, VideoParamsDto params) {
        UserLabel userLabel = userLabelVo.getUserLabel();
        String[] duty = userLabelVo.getDuty().split(",");
        if (StringUtils.isNotEmpty(params.getVideoDuty())) {
            duty = params.getVideoDuty().split(",");
            if (StringUtils.isNotEmpty(params.getLastCatId())) {
                String catIdsSort = userLabelVo.getUserLabel().getCatIdsSort();
                catIdsSort = VideoUtils.appendCatIds(params.getLastCatId(), catIdsSort);
                userLabel.setCatIdsSort(catIdsSort);
                duty = VideoUtils.getDefaultDuty().split(",");
            } else if ("0".equals(duty[0])) {
                //如果主标签变成0，进行主从标签切换
                userLabelService.calculateCatIdsSort(userLabel, params.getAppPackage());
                duty = VideoUtils.getDefaultDuty().split(",");
            }
            userLabelService.updateDBAndRedis(userLabel);
        }
        return duty;
    }

    /**
     * 首页推荐视频1.8
     *
     * @param params
     * @return
     */
    @Override
    public FirstVideos161Vo firstRecommendVideosList18(VideoParamsDto params, AbTestAdvParamsVo queueVo) {
        //LZHONG
        FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        //获取用户标签
        UserLabelVo userLabelVo = userLabelService.getUserLabelVoByDeviceId(params.getDeviceId(), params.getAppPackage());
        //获取占比和用户标签集合
        String[] duty = getDuty(userLabelVo, params);
        String catIdsSorts = userLabelVo.getUserLabel().getCatIdsSort();
        if (StringUtil.isBlank(catIdsSorts)) {
            return firstVideosNewVo;
        }
        String[] catIdsSort = catIdsSorts.split(",");
        //查询视频数据和广告数据, 记录已经浏览ID
        List<Videos161Vo> firstVideos = getFirstRecommendVideos(params, duty, catIdsSort, queueVo);
        if (isNotEmpty(firstVideos)) {
            videoCacheService.fillParams(firstVideos);
            //视频作者头像和名字取用户表（虚拟用户或者真是用户）
            clUserService.packagingUserAndVideos(firstVideos);
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption1(firstVideos, videoAlbumVos, params.getMobileType(), params.getAppVersion());
        }
        //是否返回广告
        if ((VersionUtil.isBetween(Constant.APPVERSION_253, Constant.APPVERSION_257, params.getAppVersion()) &&
                Constant.ANDROID.equals(params.getMobileType())) ||
                Constant.WeChat.equals(params.getMobileType())) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else if("2".equals(params.getIsCombine())) {
            //双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else {
            //随机2个广告
            String channelId = params.getChannelId();
            params.setChannelId(ChannelUtil.filter(channelId));
            Map<String, Object> adverMap = new HashMap<>();
            String appVersion = params.getAppVersion();
            adverMap.put("marketChannelId", params.getChannelId());
            adverMap.put("channelId", params.getChannelId());
            adverMap.put("appVersion", appVersion);
            adverMap.put("positionType", params.getPositionType());
            adverMap.put("mobileType", params.getMobileType());
            adverMap.put("permission", params.getPermission());
            adverMap.put("appPackage", PackageUtil.getAppPackage(params.getAppPackage(), params.getMobileType()));

            boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
            if (newFlag) {
                //V2.5.0走新广告逻辑
                packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, adverMap, queueVo);
            } else {
                List<AdvertVo> advertList = advertOldService.getAdvertsBySection(adverMap);
                VideoUtils.packagAdvertAndVideos(firstVideosNewVo, firstVideos, advertList, appVersion);
            }
        }
        //重新返回占比给前端
        firstVideosNewVo.setVideoDuty(StringUtils.join(duty, ","));
        return firstVideosNewVo;
    }

    //V2.5.0走新广告与视频组合
    private void packagNewAdvertAndVideos(FirstVideos161Vo firstVideosNewVo, List<Videos161Vo> firstVideos, Map<String, Object> adverMap, AbTestAdvParamsVo queueVo) {
        List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, adverMap);
        List<FirstVideosVo> firstVideosVos = VideoUtils.packagingNewAdvert(advertCodeVos, firstVideos);
        firstVideosNewVo.setFirstVideosVos(firstVideosVos);
    }

    /**
     * 获取第二标签的分类ID，不够需要随机补齐
     * 2个情况：
     * a.用户标签数>=视频占比，直接返回，例如 用户第二标签：cat1，cat2，cat3，cat4，num=4
     * b.用户标签数<视频占比，需要补齐，例如 用户第二标签：cat1，cat2，num=4，需要补齐2个
     *
     * @param catIdsSort
     * @param num
     * @return
     */
    public List getCatIds(List<String> catIdsSort, int num, String appPackage) {
        List<String> bak = new ArrayList(catIdsSort);
        int size = bak.size() - 1;//去掉主标签的catID
        bak.remove(0);
        if (size >= num) {
            return bak.subList(0, num);
        } else {
            int less = num - size;
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("otherCatIds", bak);
            paramMap.put("appPackage", appPackage);
            List<String> catIds = videosCatMapper.findCatIdsNotIn(paramMap);
            Collections.shuffle(catIds);
            if (catIds.size() < less) {
                log.error("获取随机视频分类失败，分类数不足7个，包名：" + appPackage);
                less = catIds.size();
            }
            return catIds.subList(0, less);
        }
    }

    public String getCatIdsByNum(List<String> catIdsSort, int num) {
        List<String> catIds = catIdsSort.subList(0, num);
        String join = String.join(",", catIds);
        return join;
    }

    /**
     * 根据用户权重和视频占比查询视频
     *
     * @param params     查询参数
     * @param duty       视频占比，例如4:3:2 表示第一标签显示4个，第二标签显示3个分类各一条，第三标签显示最近3天视频，视频分类有可能包含前面二标签
     * @param catIdsSort 用户标签权重，有序，例如"catID1，catID2，catID3"，权重关系catID1>catID2>catID3
     * @return
     */
    private List<Videos161Vo> getFirstRecommendVideos(
            VideoParamsDto params, String[] duty, String[] catIdsSort, AbTestAdvParamsVo queueVo) {
        List<Videos161Vo> firstVideosAll = new ArrayList<Videos161Vo>();
        boolean isOpen = false;//市场审核开关是否开启；
        boolean isLogin = StringUtils.isNotBlank(params.getUserId()) && !"0".equals(params.getUserId());
        int state = Global.getInt("use_EsSearch_state");//0-使用DB查询，1-使用ES查询
        //第三标签视频
        //先过滤市场审核开关屏蔽的分类
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(params.getChannelId(), params.getAppVersion());
        String otherCatIds = marketAudit == null || StringUtils.isEmpty(marketAudit.getCatIds()) ? "" : marketAudit.getCatIds();
        //V2.5.6 推荐列表合集屏蔽
        if (marketAudit != null && StringUtils.isNotBlank(marketAudit.getGatherIds())) {
            params.setGatherIds(marketAudit.getGatherIds());
        }
        //审核开关开启并且和用户标签有重叠
        if (StringUtils.isNotEmpty(otherCatIds) && VideoUtils.containsHiddenCatId(otherCatIds, catIdsSort)) {
            isOpen = true;
            duty = new String[]{"0", "0", "8"};
        }
        //屏蔽不生效分类
        List<String> catIds = videosCatMapper.queryCatIdsList(params.getAppPackage());
        //推荐列表屏蔽个人用户上传视频
        catIds.add("1000");
        if (isNotEmpty(catIds)) {
            if (StringUtils.isNotEmpty(otherCatIds)) {
                otherCatIds += "," + String.join(",", catIds);
            } else {
                otherCatIds = String.join(",", catIds);
            }
        }
        params.setOtherCatIds(otherCatIds);
        Map<String, Object> mapParams = EntityUtils.entityToMap(params);
        //激励视频配置参数
        Map<String, Object> incentiveMap = AdvUtils.getIncentiveInfo(params.getMobileType(), params.getAppPackage(), queueVo,0);
        String isShowIncentive = incentiveMap.get("isShowIncentive")==null? "0" : incentiveMap.get("isShowIncentive").toString();
        Integer position = incentiveMap.get("position")==null? 0 : Integer.valueOf(incentiveMap.get("position").toString());
        String incentiveVideoRate = incentiveMap.get("incentiveVideoRate")==null? "0" : incentiveMap.get("incentiveVideoRate").toString();

        List<Videos161Vo> firstVideos0 = new ArrayList<>();
        if (Constant.OPENSTR.equals(isShowIncentive) && VersionUtil.compareIsHigh(params.getAppVersion(), Constant.APPVERSION_258) && Integer.parseInt(duty[0]) > 2) {
            firstVideos0 = getFirstVideosByParams(mapParams, RedisKeyConstant.INCENTIVEVIDEO, duty, state, catIdsSort);
            if (isNotEmpty(firstVideos0)) {
                if (firstVideos0.get(0).getLabel() == 2) {
                    String[] newCatIdsSort = new String[catIdsSort.length - 1];
                    int n = 0;
                    for (int j = 0; j < catIdsSort.length; j++) {
                        if (!String.valueOf(firstVideos0.get(0).getCatId()).equals(catIdsSort[j])) {
                            newCatIdsSort[n] = catIdsSort[j];
                            n++;
                        }
                    }
                    catIdsSort = newCatIdsSort;
                }
            }
        }
        mapParams.put("incentive", 0);
        List<Videos161Vo> firstVideosList3 = Lists.newArrayList();
        /*if (CollectionUtils.isNotEmpty(firstVideosList3)) {
            VideoUtils.setLabel(firstVideosList3, 3);
            firstVideosAll.addAll(firstVideosList3);
        }*/
        //市场审核开关开启-不查询第一二标签数据
        String[] newduty = duty;
        if (!isOpen) {
            //ES开启，并且用户未登录，使用ES查询
            if (state == 1 && !isLogin) {
                Map<String, Object> mapParams1 = Maps.newHashMap(mapParams);
                CompletableFuture<Integer> f0 = CompletableFuture.supplyAsync(() -> {
                    List<Videos161Vo> firstVideosList1 = getFirstVideosByParams(mapParams1, RedisKeyConstant.CREATED3DAY, Integer.parseInt(newduty[2]), state);
                    if (isNotEmpty(firstVideosList1)) {
                        firstVideosList3.addAll(firstVideosList1);
                        firstVideosList1 = null;
                    }
                    return 0;
                }, executor);
                //第一标签视频
                String cat1 = catIdsSort[0];
                if (log.isDebugEnabled()) {
                    log.debug("推荐主标签：{}", cat1);
                }
                Map<String, Object> mapParams2 = Maps.newHashMap(mapParams);
                CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
                    List<Videos161Vo> firstVideosList1 = getFirstVideosByParams(mapParams2, cat1, Integer.parseInt(newduty[0]), state);
                    if (isNotEmpty(firstVideosList1)) {
                        VideoUtils.setLabel(firstVideosList1, 1);
                        firstVideosAll.addAll(firstVideosList1);
                    }
                    return 0;
                }, executor);
                //第二标签视频
                List<String> cats = Arrays.asList(catIdsSort);
                List<String> cat2 = getCatIds(cats, Integer.parseInt(duty[1]), params.getAppPackage());
                if (log.isDebugEnabled()) {
                    log.debug("推荐标签：{}", JSON.toJSONString(cat2));
                }

                List<CompletableFuture<Integer>> listFeture = cat2.stream().map(e -> {
                    return CompletableFuture.completedFuture(e).thenApplyAsync(catId -> {
                        if (log.isDebugEnabled()) {
                            log.debug("副标签：{}", catId);
                        }
                        Map<String, Object> tmp = Maps.newHashMap(mapParams);
                        List<Videos161Vo> firstVideosList2 = getFirstVideosByParams(tmp, catId, 1, state);
                        if (isNotEmpty(firstVideosList2)) {
                            VideoUtils.setLabel(firstVideosList2, 2);
                            firstVideosAll.addAll(firstVideosList2);
                        }
                        return 0;
                    }, executor);
                }).collect(Collectors.toList());
                listFeture.add(f0);
                listFeture.add(f1);
                CompletableFuture<Void> allFe = CompletableFuture.allOf(listFeture.toArray(new CompletableFuture[listFeture.size()]));
                allFe.join();
            } else {
                List<Videos161Vo> tmp = getFirstVideosByParams(mapParams, RedisKeyConstant.CREATED3DAY, Integer.parseInt(duty[2]), state);
                if (isNotEmpty(tmp)) {
                    firstVideosList3.addAll(tmp);
                    tmp.clear();
                    tmp = null;
                }
                long catId1 = Long.parseLong(catIdsSort[0]);
                //非激励视频
                String incentive = "0";
                String sql = VideoSQLUtils.getSQL(redisDB8Service, params, catIdsSort, duty, incentive);
                List<Videos161Vo> videos1And2 = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
                VideoUtils.setLabel(videos1And2, catId1);
                firstVideosAll.addAll(videos1And2);
                //已曝光ID进行缓存
                saveShowedIds(firstVideosAll, params, catId1 + "");
            }
        } else {
            List<Videos161Vo> tmp = getFirstVideosByParams(mapParams, RedisKeyConstant.CREATED3DAY, Integer.parseInt(duty[2]), state);
            if (isNotEmpty(tmp)) {
                firstVideosList3.addAll(tmp);
                tmp.clear();
                tmp = null;
            }
        }
        //如果大于2.5版本，则不返回集合其余信息
        List<Videos161Vo> radaVideos = firstVideosAll.stream().filter(e -> e != null).collect(Collectors.toList());
        firstVideosAll.clear();
        if (isNotEmpty(radaVideos)) {
            if (VersionUtil.compareIsHigh(Constant.APPVERSION_250, params.getAppVersion())) {
                //2.1.0新增视频合集展示
                fillGatherVideos(radaVideos);
            }
            //Collections.shuffle(radaVideos);
            //视频权重倒叙排序
            radaVideos.sort(Comparator.comparing(Videos161Vo::getTotalWeight).reversed());
        }
        if (isNotEmpty(firstVideos0)) {
            firstVideos0.get(0).setIncentiveRate(Double.parseDouble(incentiveVideoRate));
            firstVideos0.get(0).setIncentiveVideo(1);
            position = position > radaVideos.size() ? radaVideos.size() : (position - 1);
            radaVideos.add(position, firstVideos0.get(0));
            if (firstVideos0.get(0).getLabel() == 1) {
                int importCatIdNum = Integer.parseInt(duty[0]) + 1;
                duty[0] = String.valueOf(importCatIdNum);
            } else if (firstVideos0.get(0).getLabel() == 3) {
                int importCatIdNum = Integer.parseInt(duty[2]) + 1;
                duty[2] = String.valueOf(importCatIdNum);
            } else {
                int importCatIdNum = Integer.parseInt(duty[1]) + 1;
                duty[1] = String.valueOf(importCatIdNum);
            }
        }
        if (isNotEmpty(firstVideosList3)) {
            VideoUtils.setLabel(firstVideosList3, 3);
            radaVideos.addAll(firstVideosList3);
        }
        return radaVideos;
    }

    /**
     * 填充合集视频子集合
     *
     * @param videos
     */
    private void fillGatherVideos(List<Videos161Vo> videos) {
        for (Videos161Vo vo : videos) {
            if (vo.getGatherId() != null && vo.getGatherId() > 0) {
                VideoGatherVo videoGatherVo = videoGatherService.getVideoGatherVo(vo);
                vo.setVideoGatherVo(videoGatherVo);
            }
        }
    }

    public List<Videos161Vo> searchVideos(Map<String, Object> params, int state) {
        String userId = params.get("userId") + "";
        if (StringUtils.isNotBlank(userId) && !"0".equals(userId)) {
            return firstVideosMapper.findFirstVideosListByUserId18(params);
        } else {
            if (state == 1) {
                log.info("use es search");
                String catId = MapUtils.getString(params, "catId");
                if ("created3Day".equals(catId)) {
                    return videoEsService.query(params);
                } else {
                    try {
                        String incentive = MapUtils.getString(params, "incentive");
                        String otherCatIds = MapUtils.getString(params, "otherCatIds");
                        String showedIds = MapUtils.getString(params, "showedIds");
                        String gatherIds = MapUtils.getString(params, "gatherIds");
                        int num = Integer.valueOf(MapUtils.getString(params, "num"));
                        List<Integer> excludeCatids = Lists.newArrayList();
                        List<String> excludeidStr = StringUtils.isNotBlank(otherCatIds) ? Lists.newArrayList(otherCatIds.split(",")) : null;
                        if (excludeidStr != null) {
                            for (String catIdStr : excludeidStr) {
                                excludeCatids.add(Integer.parseInt(catIdStr));
                            }
                        }
                        List<Integer> excludeCollectids = Lists.newArrayList();
                        List<String> gatherIdStr = StringUtils.isNotBlank(gatherIds) ? Lists.newArrayList(gatherIds.split(",")) : null;
                        if (gatherIdStr != null) {
                            for (String gatherId : gatherIdStr) {
                                excludeCollectids.add(Integer.parseInt(gatherId));
                            }
                        }
                        List<String> ids = Lists.newArrayList();
                        if ("0".equals(incentive)) {
                            ids = findVideosService.getVideoId(showedIds, Integer.valueOf(catId), num, excludeCollectids);
                        } else {
                            List<IncentiveVideoHotspot> idList = findJLVideosService.getVideoId(showedIds, Integer.valueOf(catId), num, excludeCatids, excludeCollectids);
                            if (idList != null) {
                                ids.add(idList.get(0).getVideo_id());
                            }
                        }
                        if (ids != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("推荐catid={}, 视频数量：{}", catId, ids.size());
                            }
                            return videoEsService.queryByIds(ids);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("推荐catid={}, 视频数量：{}", catId, 0);
                            }
                        }
                    } catch (Exception e) {
                        log.error("查询monogo数据失败！");
                    }
                    return null;
                }
            } else {
                return firstVideosMapper.findFirstVideosList18(params);
            }
        }
    }

    private List<Videos161Vo> getFirstVideosByParams(Map<String, Object> params, String catId, int num, int state) {
        if (num == 0){
            return null;
        }
        String key = RedisKeyConstant.SHOWEDIDS_KEY + params.get("deviceId") + ":" + catId;
        String showedIds = redisDB8Service.get(key);
        if (showedIds != null) {
            if (showedIds.split(",").length > 100) {
                redisDB8Service.del(key);
                showedIds = null;
            }
        }
        params.put("showedIds", VideoUtils.appendShowedIds(showedIds, params.get("id") + ""));//视频详情接口要过滤当前id
        params.put("catId", catId);
        params.put("num", num);
        List<Videos161Vo> videos161Vos = searchVideos(params, state);
        //未曝光视频不能满足返回条件，清空曝光记录
        if (videos161Vos == null || videos161Vos.size() < num) {
            params.put("showedIds", null);
            videos161Vos = searchVideos(params, state);
            redisDB8Service.del(key);
        }
        if (isNotEmpty(videos161Vos)) {
            List<String> ids_list = videos161Vos.stream().map(p -> p.getId() + "").collect(Collectors.toList());
            String newIds = VideoUtils.getVideoIds(ids_list, showedIds);
            redisDB8Service.set(key, newIds, RedisKeyConstant.SHOWEDIDS_SECONDS);
        }
        return videos161Vos;
    }

    /**
     * 激励视频查询
     */
    private List<Videos161Vo> getFirstVideosByParams(Map<String, Object> params, String catId, String[] duty, int state, String[] catIdsSort) {
        String[] catIdsSortAll = new String[catIdsSort.length + 1];
        for (int i = 0; i < catIdsSort.length; i++) {
            catIdsSortAll[i] = catIdsSort[i];
        }
        catIdsSortAll[catIdsSort.length] = RedisKeyConstant.CREATED3DAY;
        String key = RedisKeyConstant.SHOWEDIDS_KEY + params.get("deviceId") + ":" + catId;
        String showedIds = redisDB8Service.get(key);
        params.put("showedIds", VideoUtils.appendShowedIds(showedIds, params.get("id") + ""));
        params.put("num", 1);
        params.put("incentive", 1);
        List<Videos161Vo> videos161Vos = null;
        videos161Vos = getIncentiveVideos(params, state, catIdsSortAll, key, showedIds, videos161Vos, duty);
        //未曝光视频不能满足返回条件，清空曝光记录
        if (videos161Vos == null || videos161Vos.size() < 1) {
            params.put("showedIds", null);
            redisDB8Service.del(key);
            videos161Vos = getIncentiveVideos(params, state, catIdsSortAll, key, null, videos161Vos, duty);
        }
        return videos161Vos;
    }

    private List<Videos161Vo> getIncentiveVideos(Map<String, Object> params, int state, String[] catIdsSortAll, String key, String showedIds, List<Videos161Vo> videos161Vos, String[] duty) {
        for (int i = 0; i < catIdsSortAll.length; i++) {
            params.put("catId", catIdsSortAll[i]);
            videos161Vos = searchVideos(params, state);
            if (isNotEmpty(videos161Vos)) {
                params.put("incentive", 0);
                if (i == 0) {
                    VideoUtils.setLabel(videos161Vos, 1);
                    int importCatIdNum = Integer.parseInt(duty[0]) - 1;
                    duty[0] = String.valueOf(importCatIdNum);
                } else if (i == (catIdsSortAll.length - 1)) {
                    VideoUtils.setLabel(videos161Vos, 3);
                    duty[2] = String.valueOf(0);
                } else {
                    int importCatIdNum = Integer.parseInt(duty[1]) - 1;
                    duty[1] = String.valueOf(importCatIdNum);
                    VideoUtils.setLabel(videos161Vos, 2);
                }
                List<String> ids_list = videos161Vos.stream().map(p -> p.getId() + "").collect(Collectors.toList());
                String newIds = VideoUtils.getVideoIds(ids_list, showedIds);
                redisDB8Service.set(key, newIds, RedisKeyConstant.SHOWEDIDS_SECONDS);
                break;
            }
        }
        return videos161Vos;
    }


    private void saveShowedIds(List<Videos161Vo> videos, VideoParamsDto params, String catId1) {
        List<String> ids_label1_list = videos.stream().filter(v -> v.getLabel() == 1).map(p -> p.getId() + "").collect(Collectors.toList());
        List<Videos161Vo> label2_list = videos.stream().filter(v -> v.getLabel() == 2).collect(Collectors.toList());
        if (isNotEmpty(ids_label1_list)) {
            updateRedis(params.getDeviceId(), ids_label1_list, catId1);
        }
        if (isNotEmpty(label2_list)) {
            Map<Long, Long> catMap = label2_list.stream().collect(Collectors.toMap(Videos161Vo::getCatId, Videos161Vo::getCatId));
            Iterator it = catMap.keySet().iterator();
            while (it.hasNext()) {
                long catId = Long.parseLong(it.next().toString());
                List<String> cat_ids_List = label2_list.stream().filter(v -> v.getCatId() == catId).map(p -> p.getId() + "").collect(Collectors.toList());
                updateRedis(params.getDeviceId(), cat_ids_List, catId + "");
            }
        }
    }

    public void updateRedis(String deviceId, List<String> ids, String catId) {
        String key = RedisKeyConstant.SHOWEDIDS_KEY + deviceId + ":" + catId;
        String showedIds = redisDB8Service.get(key);
        String newIds = VideoUtils.getVideoIds(ids, showedIds);
        redisDB8Service.set(key, newIds, RedisKeyConstant.SHOWEDIDS_SECONDS);
    }

    @Override
    public FirstVideos161Vo findRecommendByTeenager(Map<String, Object> params) {
        String deviceId = (String) params.get("deviceId");
        int pageNums = VideoUtils.getPageNums();
        String channelId = params.get("channelId").toString();
        String appVersion = params.get("appVersion").toString();
        params.put("queryNumber", pageNums);
        //如果开启青少年模式，需要获取屏蔽分类ID
        String otherCatIds = marketAuditService.getCatIdsByChannelIdAndAppVersionFromTeenager(channelId, appVersion);
        List<String> marketAuditcatIds = new ArrayList();
        if (StringUtils.isNotEmpty(otherCatIds)) {
            marketAuditcatIds = Arrays.asList(otherCatIds.split(","));
        }
        List<String> catIds = new ArrayList<>(marketAuditcatIds);
        //屏蔽不生效分类
        String appPackage = MapUtils.getString(params, "appPackage");
        List<String> catIdsList = videosCatMapper.queryCatIdsList(appPackage);
        if (isNotEmpty(catIdsList)) {
            for (String catId : catIdsList) {
                if (!catIds.contains(catId)) {
                    catIds.add(catId);
                }
            }
        }
        params.put("otherCatIds", catIds);
        List<Videos161Vo> videos = query(params);
        String mobileType = MapUtils.getString(params, "mobileType");
        //根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
        VideoUtils.videoEncryption1(videos, videoAlbumVos, mobileType, appVersion);
        return VideoUtils.packaging(videos);
    }

    @Override
    public FirstVideos161Vo findNoRecommendByTeenager(Map<String, Object> params) {
        String deviceId = (String) params.get("deviceId");
        int pageNums = VideoUtils.getPageNums();
        params.put("queryNumber", pageNums);
        String appPackage = MapUtils.getString(params, "appPackage");
        List<String> catIds = videosCatMapper.queryCatIdsList(appPackage);
        params.put("otherCatIds", catIds);
        List<Videos161Vo> videos = query(params);
        String mobileType = MapUtils.getString(params, "mobileType");
        String appVersion = MapUtils.getString(params, "appVersion");
        //根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
        VideoUtils.videoEncryption1(videos, videoAlbumVos, mobileType, appVersion);
        return VideoUtils.packaging(videos);
    }

    @Override
    public FirstVideoDetailVo firstVideosDetailList25(AbTestAdvParamsVo queueVo, Map<String, Object> params) {
        FirstVideoDetailVo firstVideosNewVo = new FirstVideoDetailVo();
        List<Videos161Vo> firstVideosVos;
        params.put("queryNumber", "1");//随机标识
        int state = Global.getInt("use_EsSearch_state3");//0-使用DB查询，1-使用ES查询
        //根据渠道和版本号进行市场屏蔽
        String appVersion = MapUtils.getString(params, "appVersion");
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(MapUtils.getString(params, "marketChannelId"), appVersion);
        String otherCatIds = marketAudit == null || StringUtils.isEmpty(marketAudit.getCatIds()) ? "" : marketAudit.getCatIds();
        //屏蔽不生效分类
        String appPackage = MapUtils.getString(params, "appPackage");
        List<String> catIds = videosCatMapper.queryCatIdsList(appPackage);
        if (isNotEmpty(catIds)) {
            if (StringUtils.isNotEmpty(otherCatIds)) {
                otherCatIds += "," + String.join(",", catIds);
            } else {
                otherCatIds = String.join(",", catIds);
            }
        }
        params.put("otherCatIds", otherCatIds);
        firstVideosVos = getFirstVideosByParams(params, params.get("catId") + "", Global.getInt("page_number"), state);
        if (isNotEmpty(firstVideosVos)) {
            videoCacheService.fillParams(firstVideosVos);
            //视频作者头像和名字取用户表（虚拟用户或者真是用户）
            clUserService.packagingUserAndVideos(firstVideosVos);
            String mobileType = MapUtils.getString(params, "mobileType");
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption1(firstVideosVos, videoAlbumVos, mobileType, appVersion);
        }
        long id = Long.parseLong(params.get("id").toString());
        if (id > 0) {
            FirstVideoEsVo firstVideoEsVo = videoEsService.getById(id);
//            //V2.5.6 屏蔽合集
//            if(marketAudit!=null && StringUtils.isNotBlank(marketAudit.getGatherIds())){
//                String gatherIds = marketAudit.getGatherIds();
//                List<String> list = Arrays.asList(gatherIds.split(","));
//                if (list.contains(String.valueOf(firstVideoEsVo.getGatherId()))){
//                    firstVideoEsVo = null;
//                }
//            }
            if (firstVideoEsVo != null) {
                Videos161Vo videos161Vo = VideoUtils.packaging(firstVideoEsVo);
                boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_322);
                if (newFlag) {
                    String albumId = MapUtils.getString(params, "albumId");
                    if (StringUtils.isNotEmpty(albumId)) {
                        videos161Vo.setAlbumId(Long.valueOf(albumId));
                    }
                    VideoGatherVo videoGatherVo = albumService.getCurrentVideoAlbums(videos161Vo, appPackage);
                    firstVideosNewVo.setVideoGatherVo(videoGatherVo);
                } else {
                    VideoGatherVo videoGatherVo = videoGatherService.getCurrentVideoGatherVo(videos161Vo);
                    firstVideosNewVo.setVideoGatherVo(videoGatherVo);
                }
            }
        }
        boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
        if (newFlag) {
            //V2.5.0走新广告逻辑
            List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, params);
            firstVideosNewVo.setAdvertCodeVos(advertCodeVos);
        } else {
            //随机5广告
            List<AdvertVo> advertList = advertOldService.getAdvertsBySection(params);
            if (isNotEmpty(advertList)) {
                AdvertVo advertVo = advertList.get(0);
                int type = advertVo.getType();
                if (type == 0) {
                    List<AdvertVo> list = AdvUtils.computer(advertList, 5);
                    firstVideosNewVo.setAdvers(list);
                } else {
                    firstVideosNewVo.setAdvers(advertList);
                }
            }
        }
        firstVideosNewVo.setVideos(firstVideosVos);
        return firstVideosNewVo;
    }

    /**
     * 用户发布视频
     *
     * @param firstVideosDto
     * @return
     */
    @Override
    public ResultMap videoPublication(FirstVideosDto firstVideosDto) {

        Map<String, Object> param = new HashMap<String, Object>(1);
        param.put("id", firstVideosDto.getUserId());
        List<ClUserVo> userVos = clUserMapper.findClUserList(param);
        if (isEmpty(userVos)) {
            return ResultMap.error(400, "用户不存在");
        }

        ClUserVo userVo = userVos.get(0);

        firstVideosDto.setVideoAuthor(userVo.getName());
        firstVideosDto.setBsyHeadUrl(userVo.getImgUrl());
        firstVideosDto.setCatId(1000);
        firstVideosDto.setState(2);
        firstVideosDto.setExamState(0);
        firstVideosDto.setUpdateType(1);
        if (StringUtil.isEmpty(firstVideosDto.getBsyAudioUrl())) {
            firstVideosDto.setBsyAudioUrl("");
        }
        if (StringUtil.isEmpty(firstVideosDto.getBsyImgUrl())) {
            firstVideosDto.setBsyImgUrl("");
        }
        if (StringUtil.isEmpty(firstVideosDto.getLocalAudioUrl())) {
            firstVideosDto.setLocalAudioUrl("");
        }
        firstVideosDto.setCreatedAt(new Date());
        int count = firstVideosMapper.saveVideo(firstVideosDto);
        if (count == 0) {
            return ResultMap.error();
        }
        VideoPublicationVo result = new VideoPublicationVo();
        result.setBsyUrl(firstVideosDto.getBsyUrl());
        return ResultMap.success(result);
    }


    /**
     * 用户视频删除
     *
     * @return
     */
    @Override
    public ResultMap videoDelete(FirstVideosDeleteDto deleteDto) {
        FirstVideos firstVideos = firstVideosMapper.getFirstVideosById(deleteDto.getId());
        int count = firstVideosMapper.deletePublicationVideoByIdAndUserIdAndAndAppPackage(deleteDto.getId(), deleteDto.getUserId());
        if (count == 0) {
            return ResultMap.error(400, "删除失败");
        }
        // 删除视频和图片
        AWSUtil4Video.delete(firstVideos.getBsyUrl(), AWSUtil4Video.FLOAD_USER_VIDEO);
        AWSUtil4Video.delete(firstVideos.getBsyImgUrl(), AWSUtil4Video.FLOAD_USER_VIDEO);
        return ResultMap.success();
    }

    @Override
    public FirstVideos getFirstVideosById(Long id) {
        FirstVideos firstVideos = firstVideosMapper.findFirstVideosById(id);
        return firstVideos;
    }

    @Override
    public ResultMap center(UserCenterDto userCenterDto) {

        // 查询用户发布视频列表
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userCenterDto.getQueryId());
//        param.put("appPackage", userCenterDto.getAppPackage());
        param.put("isOwn", userCenterDto.getIsOwn());

        UserCenterVo centerVo = null;
        if (userCenterDto.getIsMoreInfo() == 1) {
            // 获取用户信息
            Map<String, Object> userParam = new HashMap<String, Object>(1);
            userParam.put("id", userCenterDto.getQueryId());
            List<ClUserVo> userVos = clUserMapper.findClUserList(userParam);
            if (isEmpty(userVos)) {
                return ResultMap.error(400, "用户不存在");
            }
            ClUserVo userVo = userVos.get(0);
            // 查询用户视频统计信息
            centerVo = firstVideosMapper.countUserCenter(param);
            centerVo.setUserId(userCenterDto.getQueryId());
            centerVo.setImgUrl(userVo.getImgUrl());
            centerVo.setName(userVo.getName());
            centerVo.setSign(userVo.getSign());
            if (centerVo.getVideosCount() == null || centerVo.getVideosCount() == 0) {
                return ResultMap.success(centerVo);
            }
        } else {
            centerVo = new UserCenterVo();
        }

        int pageSize = userCenterDto.getPageSize() == null ? 8 : userCenterDto.getPageSize();
        PageHelper.startPage(userCenterDto.getPageNum(), pageSize);
        List<FirstVideosPublicationVo> videoList = firstVideosMapper.selectPublicationVideoByUserId(param);
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
        for (FirstVideosPublicationVo publicationVo : videoList) {
            Long albumId = VideoUtils.queryAlbumId(publicationVo.getId(), "3.2.30");
            publicationVo.setAlbumId(albumId);
            if (albumId != null && albumId > 0L) {
                videoAlbumVos.forEach(a -> {
                    if (a.getId().equals(albumId)) {
                        publicationVo.setAlbumTitle(a.getTitle());
                    }
                });
            }
        }
        centerVo.setVideosList(videoList);
        return ResultMap.success(centerVo);
    }

    @Override
    public FirstVideos161Vo immerseVideosList(String deviceId, String channelId, String publicInfo) {
        String uuid = "";
        if (StringUtils.isNotBlank(publicInfo)) {
            PublicInfo pbInfo  = new PublicInfo(publicInfo);
            uuid = pbInfo.getUuid();
        }
        //获取用户3个标签
        List<Integer> userCatIds;
        if (StringUtils.isNotEmpty(uuid)) {
            userCatIds = findRecommendCatidService.getUserCatids(uuid, channelId);
        } else {
            userCatIds = findRecommendCatidService.getUserCatids(deviceId, channelId);
        }
        if (CollectionUtils.isNotEmpty(userCatIds)) {
            String catIds = userCatIds.stream().map(c -> c.intValue() + "" ).collect(Collectors.joining(","));
            List<String> videoIdList = predictService.getImmerseVideoIds(catIds);
            if (CollectionUtils.isNotEmpty(videoIdList)) {
                String videoIds = videoIdList.stream().collect(Collectors.joining(","));
                List<Videos161Vo> resultList = videoEsService.getVideoByIdList(videoIds);
                if (CollectionUtils.isEmpty(resultList)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("showedIds", videoIds);
                    resultList = firstVideosMapper.findFirstVideosListById(map);
                }
                return VideoUtils.packaging(resultList);
            }
        }
        return null;
    }

    @Override
    public List<VideosInfoVo> getFirstVideosByIds(VideosParamVo paramVo) {
        List<VideosInfoVo> videosInfoVoList = Lists.newArrayList();
        List<String> videoIds = paramVo.getVideoIds().stream().map(String::valueOf).collect(Collectors.toList());
        List<Videos161Vo> firstVideos = findRecommendEsService.list(videoIds, "0");
        for (Videos161Vo videos161Vo : firstVideos) {
            VideosInfoVo infoVo = new VideosInfoVo();
            BeanUtils.copyProperties(videos161Vo, infoVo);
            videosInfoVoList.add(infoVo);
        }
        return videosInfoVoList;
    }

    @Override
    public void saveVideoCache(String videoIds) {
        List<Videos161Vo> firstVideos = firstVideosMapper.findSpecialVideosListById(videoIds);
        for (Videos161Vo videos161Vo : firstVideos) {
            String key = RedisKeyConstant.SPECIAL_VIDEO_LIST + videos161Vo.getId();
            redisDB8Service.set(key, JSON.toJSONString(videos161Vo), -1);
        }
    }

    @Override
    public void flashCacheToRedis(String options, String cacheStr) {
        if ("add".equals(options)) {
            String[] contents = cacheStr.split("@");
            String key = contents[0];
            String value = contents[1];
            redisDB8Service.set(key, value, -1);
        } else if ("delete".equals(options)) {
            if (cacheStr.contains("*")) {
                Set<String> allKeys = redisDB8Service.keys(cacheStr);
                for (String keys : allKeys) {
                    redisDB8Service.del(keys);
                }
            } else {
                redisDB8Service.del(cacheStr);
            }
        }
    }

    public List<Videos161Vo> query(Map<String, Object> params) {
        String userId = params.get("userId").toString();
        List<Videos161Vo> firstVideos;
        if (null == userId || "0".equals(userId)) {
            firstVideos = firstVideosMapper.findFirstVideosList161(params);
        } else {
            firstVideos = firstVideosMapper.findFirstVideosListByUserId161(params);
        }
        if (isNotEmpty(firstVideos)) {
            //VideoUtils.setLoveAndWatchNum(firstVideos);
            //赋值分类名称，神策埋点需要
            VideoUtils.setCatName(firstVideos, null, videoCacheService.getVideosCatMap(VideoContant.FIRST_VIDEO_CODE));
        }
        return firstVideos;
    }

    //V1.8.0市场屏蔽功能
    private void marketAudit18(String marketChannelId, String appVersion, Map<String, Object> params) {
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(marketChannelId, appVersion);
        if (marketAudit != null) {
            if (StringUtils.isNotEmpty(marketAudit.getCatIds())) {
                String[] split = marketAudit.getCatIds().split(",");
                List<Long> list = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    list.add(Long.valueOf(split[i]));
                }
                params.put("otherCatIds", list);
            }
            //v2.1.0
            if (StringUtils.isNotEmpty(marketAudit.getGatherIds())) {
                params.put("gatherIds", marketAudit.getGatherIds());
            }
        }
    }

}