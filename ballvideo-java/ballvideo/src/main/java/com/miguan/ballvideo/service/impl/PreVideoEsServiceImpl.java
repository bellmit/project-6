//package com.miguan.ballvideo.service.impl;
//
//import com.google.common.collect.Lists;
//import com.miguan.ballvideo.common.constants.Constant;
//import com.miguan.ballvideo.common.enums.VideoESOptions;
//import com.miguan.ballvideo.common.util.EntityUtils;
//import com.miguan.ballvideo.common.util.Global;
//import com.miguan.ballvideo.common.util.ResultMap;
//import com.miguan.ballvideo.common.util.VersionUtil;
//import com.miguan.ballvideo.common.util.adv.AdvUtils;
//import com.miguan.ballvideo.common.util.video.VideoUtils;
//import com.miguan.ballvideo.dto.VideoGatherParamsDto;
//import com.miguan.ballvideo.dynamicquery.DynamicQuery;
//import com.miguan.ballvideo.entity.MarketAudit;
//import com.miguan.ballvideo.entity.VideoGather;
//import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
//import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
//import com.miguan.ballvideo.repositories.VideoEsRepository;
//import com.miguan.ballvideo.service.AdvertOldService;
//import com.miguan.ballvideo.service.AdvertService;
//import com.miguan.ballvideo.service.ClUserService;
//import com.miguan.ballvideo.service.DeviceVideoLogService;
//import com.miguan.ballvideo.service.MarketAuditService;
//import com.miguan.ballvideo.service.RedisDB6Service;
//import com.miguan.ballvideo.service.ToolMofangService;
//import com.miguan.ballvideo.service.VideoCacheService;
//import com.miguan.ballvideo.service.VideoEsService;
//import com.miguan.ballvideo.service.VideoGatherService;
//import com.miguan.ballvideo.vo.AdvertCodeVo;
//import com.miguan.ballvideo.vo.AdvertVo;
//import com.miguan.ballvideo.vo.video.FirstVideosVo;
//import com.miguan.ballvideo.vo.video.VideoGatherVo;
//import com.miguan.ballvideo.vo.video.Videos161Vo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.MatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.TermsQueryBuilder;
//import org.elasticsearch.script.Script;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.elasticsearch.search.sort.FieldSortBuilder;
//import org.elasticsearch.search.sort.ScriptSortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.SearchResultMapper;
//import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
//import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
//import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
//import org.springframework.data.elasticsearch.core.query.SearchQuery;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
//
///**
// * @Author shixh
// * @Date 2020/1/7
// **/
//@Slf4j
//@Service
//public class PreVideoEsServiceImpl  {
//
//    @Resource(name="redisDB6Service")
//    private RedisDB6Service redisDB6Service;
//
//    @Resource
//    private ElasticsearchTemplate esTemplate;
//
//    @Resource
//    private VideoEsRepository videoEsRepository;
//
//    @Resource
//    private VideoCacheService videoCacheService;
//
//    @Resource
//    private DynamicQuery dynamicQuery;
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Resource
//    private VideoGatherService videoGatherService;
//
//    @Resource
//    private MarketAuditService marketAuditService;
//
//    @Resource
//    private AdvertOldService advertOldService;
//
//    @Resource
//    private AdvertService advertService;
//
//    @Resource
//    private ToolMofangService toolMofangService;
//
//    @Resource
//    private ClUserService clUserService;
//
//    @Resource
//    private DeviceVideoLogService deviceVideoLogService;
//
//    private String video_sql = "select v.id,v.title,v.cat_id,v.url_img,v.bsy_url AS bsyUrl,v.created_at AS createdAt," +
//            "UNIX_TIMESTAMP(v.created_at) as createDate,v.bsy_img_url AS bsyImgUrl,v.collection_count AS collectionCount," +
//            "(v.love_count + v.love_count_real) AS loveCount,v.comment_count AS commentCount,v.is_incentive AS incentiveVideo," +
//            "v.user_id,IFNULL(g.id, 0) AS gatherId,g.title as gatherTitle," +
//            "(v.watch_count + v.watch_count_real) AS watchCount,'0' collection,'0' love,v.bsy_head_url AS bsyHeadUrl," +
//            "v.video_author AS videoAuthor,v.video_time,v.video_size AS videoSize,v.base_weight+v.real_weight as totalWeight " +
//            "FROM first_videos v left join video_gather g ON g.id = v.gather_id AND g.state = 1 where v.state = 2 ";
//
//
//    @Override
//    public void saveToEs(List<Videos161Vo> videos) {
//        try {
//            videoCacheService.fillParams(videos);
//            List<FirstVideoEsVo> csVideos = new ArrayList<FirstVideoEsVo>();
//            for (Videos161Vo video : videos) {
//                FirstVideoEsVo csVideo = new FirstVideoEsVo();
//                BeanUtils.copyProperties(video, csVideo);
//                csVideo.setTotalWeightLimit(VideoUtils.getTotalWeightLimit(csVideo.getTotalWeight(),csVideo.getId()));
//                csVideos.add(csVideo);
//            }
//            videoEsRepository.saveAll(csVideos);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//
//    @Override
//    public void save(FirstVideoEsVo vo) {
//        vo.setTotalWeightLimit(VideoUtils.getTotalWeightLimit(vo.getTotalWeight(),vo.getId()));
//        videoEsRepository.save(vo);
//    }
//
//    @Override
//    public Object updateByGatgherId(Long gatherId) {
//        String sql = video_sql + " and v.gather_id =" + gatherId;
//        List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
//        if (CollectionUtils.isNotEmpty(videos)) {
//            saveToEs(videos);
//        }
//        return ResultMap.success();
//    }
//
//    @Override
//    public Object getMyGatherVidesoById(Long videoId) {
//        String sql = video_sql + " and v.id = " + videoId;
//        List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
//        if (CollectionUtils.isNotEmpty(videos)) {
//            Videos161Vo videos161Vo = videos.get(0);
//            return videoGatherService.getVideoGatherVo(videos161Vo);
//        }
//        return null;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Object search(String title, String userId, VideoGatherParamsDto param) {
//        String appVersion = param.getAppVersion();
//        boolean isHigh = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_231);
//        int currentPage = param.getCurrentPage() - 1;//es从0开始
//        if (currentPage < 0) {
//            currentPage = 0;
//        }
//        Pageable pageable = PageRequest.of(currentPage, param.getPageSize());
//        List<Videos161Vo> esVideos = getVideos(pageable, title, param);
//        //根据用户视频关联表判断是否收藏
//        videoCacheService.getVideosCollection(esVideos, userId);
//        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
//        clUserService.packagingUserAndVideos(esVideos);
//
//        String deviceId = param.getDeviceId();
//        for (Videos161Vo firstVideosVo : esVideos) {
//            /** 推荐算法优化 调用更新 video_id 的 show 方法*/
//            deviceVideoLogService.saveVideoInfoToRedis(deviceId, firstVideosVo.getId());
//            /** 推荐算法优化 用户点击视频，更新点击数量到 redis 结束 */
//        }
//
//        if (!isHigh) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("searchData", esVideos);
//            map.put("page", pageable);
//            return map;
//        }
//        boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
//        List<FirstVideosVo> videos;
//        if (newFlag) {
//            //V2.5.0
//            Map<String, Object> map = EntityUtils.entityToMap(param);
//            List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(map);
//            videos = VideoUtils.packagingNewBySearch(advertCodeVos, esVideos);
//        } else {
//            List<AdvertVo> advs = getAdvs(param);
//            videos = VideoUtils.packagingBySearch(advs, esVideos);
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("searchData", videos);
//        map.put("page", pageable);
//        return map;
//    }
//
//    public List<AdvertVo> getAdvs(VideoGatherParamsDto params) {
//        Map<String, Object> map = EntityUtils.entityToMap(params);
//        boolean flag = toolMofangService.stoppedByMofang(map);
//        if (flag){
//            return Lists.newArrayList();
//        }
//        List<AdvertVo> advs = advertOldService.getBaseAdverts(map);
//        if (CollectionUtils.isNotEmpty(advs)) {
//            advs = AdvUtils.computer(advs, advs.size());
//        }
//        return advs;
//    }
//
//    public List<Videos161Vo> getVideos(Pageable pageable, String title, VideoGatherParamsDto param) {
//        String preTag = "<font color='#FF5765'>";
//        String postTag = "</font>";
//        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(param.getChannelId(), param.getAppVersion());
//        //title
//        MatchQueryBuilder matchQueryBuilder = matchQuery("title", title);
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        //catid
//        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getCatIds())) {
//            TermsQueryBuilder builder = QueryBuilders.termsQuery("catId", marketAudit.getCatIds().split(","));
//            queryBuilder.mustNot(builder);
//        }
//        //屏蔽激励视频
//        queryBuilder.must(matchQuery("incentiveVideo", "0"));
//        //V2.5.6 屏蔽合集
//        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getGatherIds())) {
//            TermsQueryBuilder builder = QueryBuilders.termsQuery("gatherId", marketAudit.getGatherIds().split(","));
//            queryBuilder.mustNot(builder);
//        }
//        queryBuilder.must(matchQueryBuilder);
//        SearchQuery searchQuery = new NativeSearchQueryBuilder().
//                withQuery(queryBuilder).
//                withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag)).build();
//        searchQuery.setPageable(pageable);
//
//
//        AggregatedPage<FirstVideoEsVo> items = esTemplate.queryForPage(searchQuery, FirstVideoEsVo.class, new SearchResultMapper() {
//            @Override
//            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
//                List<Videos161Vo> chunk = new ArrayList<Videos161Vo>();
//                for (SearchHit searchHit : response.getHits()) {
//                    if (response.getHits().getHits().length <= 0) {
//                        return null;
//                    }
//                    Long id = Long.parseLong(searchHit.getId());
//                    FirstVideoEsVo firstVideoEsItem = getById(id);
//                    HighlightField highlightField = searchHit.getHighlightFields().get("title");
//                    if (highlightField != null) {
//                        firstVideoEsItem.setColorTitle(highlightField.fragments()[0].toString());
//                        Videos161Vo videos161Vo = new Videos161Vo();
//                        BeanUtils.copyProperties(firstVideoEsItem, videos161Vo);
//                        if (videos161Vo.getGatherId() > 0) {
//                            VideoGatherVo videoGatherVo = videoGatherService.getVideoGatherVoByGatherId(videos161Vo.getGatherId(), false);
//                            videos161Vo.setVideoGatherVo(videoGatherVo);
//                        }
//                        chunk.add(videos161Vo);
//                    }
//                }
//                if (chunk.size() > 0) {
//                    return new AggregatedPageImpl<>((List<T>) chunk);
//                }
//                return null;
//            }
//        });
//        if (items == null){
//            return new ArrayList<Videos161Vo>();
//        }
//        PageImpl<Videos161Vo> page = new PageImpl(items.getContent());
//        return items == null ? new ArrayList<Videos161Vo>() : page.getContent();
//    }
//
//    @Override
//    public Object update(String videoIds, String options) {
//        //modify by zhongli 2020/08/10 添加directVideoAdd类型
//        if (VideoESOptions.videoAdd.name().equals(options) || VideoESOptions.directVideoAdd.name().equals(options)) {
//            //add by zhongli 2020/08/10 要删除预上线索引数据
//            if(VideoESOptions.videoAdd.name().equals(options)){
//                //LZHONG
//            }else{
//                String sql = video_sql + " and v.id in(" + videoIds + ")";
//                List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
//                if (CollectionUtils.isNotEmpty(videos)) {
//                    saveToEs(videos);
//                }
//            }
//        } else if (VideoESOptions.videoDelete.name().equals(options)) {
//            String[] ids = videoIds.split(",");
//            for (String id : ids) {
//                long videoId = Long.parseLong(id);
//                videoEsRepository.deleteById(videoId);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public Object updateByGatherId(Long gatherId, String videoIds) {
//        List<String> vodeoIds_arrA = Arrays.asList(videoIds.split(","));
//        for (String strId : vodeoIds_arrA) {
//            Long id = Long.parseLong(strId);
//            FirstVideoEsVo firstVideoEsVo = getById(id);
//            if (firstVideoEsVo != null) {
//                firstVideoEsVo.setGatherId(gatherId);
//                if (gatherId > 0) {
//                    VideoGather gather = videoGatherService.getByGatherId(gatherId);
//                    if (gather != null) {
//                        firstVideoEsVo.setGatherTitle(gather.getTitle());
//                    } else {
//                        firstVideoEsVo.setGatherTitle("");
//                        firstVideoEsVo.setGatherId(0L);
//                    }
//                }
//                videoEsRepository.save(firstVideoEsVo);
//            }
//        }
//        return ResultMap.success();
//    }
//
//    @Override
//    public void deleteOrCloseGather(long gatherId, int state) {
//        //state:0-关闭,1-删除
//        List<FirstVideoEsVo> videos = videoEsRepository.findByGatherId(gatherId);
//        if (CollectionUtils.isNotEmpty(videos)) {
//            for (FirstVideoEsVo video : videos) {
//                if (state == 0 || state == 1) {
//                    video.setGatherId(0L);
//                    video.setGatherTitle("");
//                }
//            }
//            videoEsRepository.saveAll(videos);
//        }
//    }
//
//    @Override
//    public void deleteDueVideos() {
//        int day = Global.getInt("init_video_mostRecentDays");
//        long seconds = System.currentTimeMillis()/1000-60*60*24*day;
//        List<FirstVideoEsVo> videos = this.videoEsRepository.findByCreateDateLessThanEqual(seconds);
//        if(CollectionUtils.isNotEmpty(videos)){
//            videoEsRepository.deleteAll(videos);
//        }
//    }
//
//    @Override
//    public List<Videos161Vo> query(Map<String, Object> params) {
//        Pageable pageable = PageRequest.of(0, 10);
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        BoolQueryBuilder builder = QueryBuilders.boolQuery();        //builder下有must、should以及mustNot 相当于sql中的and、or以及not
//        String catId = MapUtils.getString(params, "catId");
//        if (StringUtils.isNotEmpty(catId) && !"0".equals(catId) && !"created3Day".equals(catId)) {
//            builder.must(matchQuery("catId", params.get("catId") + ""));
//        }
//        String incentive = MapUtils.getString(params, "incentive");
//        if (StringUtils.isNotEmpty(incentive)) {
//            builder.must(matchQuery("incentiveVideo", params.get("incentive") + ""));
//        }
//        String otherCatIds = MapUtils.getString(params, "otherCatIds");
//        if (StringUtils.isNotEmpty(otherCatIds)) {
//            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("catId", params.get("otherCatIds").toString().split(","));
//            builder.mustNot(termsQueryBuilder);
//        }
//        String showedIds = MapUtils.getString(params, "showedIds");
//        if (StringUtils.isNotEmpty(showedIds) && !"null".equals(showedIds)) {
//            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("id", params.get("showedIds").toString().split(","));
//            builder.mustNot(termsQueryBuilder);
//        }
//        //V2.5.6ES屏蔽集合
//        String gatherIds = MapUtils.getString(params, "gatherIds");
//        if (StringUtils.isNotEmpty(gatherIds) && !"null".equals(gatherIds)) {
//            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("gatherId", params.get("gatherIds").toString().split(","));
//            builder.mustNot(termsQueryBuilder);
//        }
//        builder.must(matchQuery("gatherId", 0));   //屏蔽全部集合视频
//        if ("created3Day".equals(catId)) {
//            FieldSortBuilder sort = SortBuilders.fieldSort("createDate").order(SortOrder.DESC);
//            nativeSearchQueryBuilder.withSort(sort);
//        }
//        if (StringUtils.isNotEmpty(catId) && !"created3Day".equals(catId)) {
//            FieldSortBuilder sort = SortBuilders.fieldSort("totalWeight").order(SortOrder.DESC);
//            nativeSearchQueryBuilder.withSort(sort);
//        }
//        String numStr = MapUtils.getString(params, "num");
//        int num = StringUtils.isEmpty(numStr) ? 0 : Integer.parseInt(numStr);
//        if (num > 0) {
//            pageable = PageRequest.of(0, num);
//        }
//        String queryNumber = MapUtils.getString(params, "queryNumber");
//        //queryNumber随机标识
//        if (StringUtils.isNotEmpty(queryNumber)) {
//            Script script = new Script("Math.random()");
//            ScriptSortBuilder scriptSortBuilder =
//                    SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
//            nativeSearchQueryBuilder.withQuery(builder).withSort(scriptSortBuilder);
//        } else {
//            nativeSearchQueryBuilder.withQuery(builder);
//        }
//        nativeSearchQueryBuilder.withPageable(pageable);
//        Iterable it = this.videoEsRepository.search(nativeSearchQueryBuilder.build());
//        List<FirstVideoEsVo> esVideos = Lists.newArrayList(it);
//        if (CollectionUtils.isNotEmpty(esVideos)) {
//            List<Videos161Vo> videos = new ArrayList<Videos161Vo>();
//            for (FirstVideoEsVo firstVideoEsVo : esVideos) {
//                Videos161Vo videos161Vo = new Videos161Vo();
//                BeanUtils.copyProperties(firstVideoEsVo, videos161Vo);
//                videos.add(videos161Vo);
//            }
//            return videos;
//        }
//        return null;
//    }
//
//    @Override
//    public List<Videos161Vo> query(Map<String, Object> params, int num) {
//        Script script = new Script("Math.random()");
//        ScriptSortBuilder scriptSortBuilder =
//                SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
//        String catId = MapUtils.getString(params, "catId");
//        Pageable pageable = PageRequest.of(0, num);
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        BoolQueryBuilder builder = QueryBuilders.boolQuery();
//        if (StringUtils.isNotEmpty(catId)) {
//            builder.must(matchQuery("catId", catId));
//        }
//        //屏蔽激励视频
//        builder.must(matchQuery("incentiveVideo", "0"));
//        //V2.5.6ES屏蔽集合
//        String channelId = MapUtils.getString(params, "channelId");
//        String appVersion = MapUtils.getString(params, "appVersion");
//        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
//        if (marketAudit != null && StringUtils.isNotBlank(marketAudit.getGatherIds())) {
//            String gatherIds = marketAudit.getGatherIds();
//            if (StringUtils.isNotEmpty(gatherIds) && !"null".equals(gatherIds)) {
//                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("gatherId", params.get("gatherIds").toString().split(","));
//                builder.mustNot(termsQueryBuilder);
//            }
//        }
//        builder.must(matchQuery("gatherId", 0));  //屏蔽全部合集视频
//        nativeSearchQueryBuilder.withQuery(builder).withSort(scriptSortBuilder);
//        nativeSearchQueryBuilder.withPageable(pageable);
//        Iterable it = this.videoEsRepository.search(nativeSearchQueryBuilder.build());
//        List<FirstVideoEsVo> esVideos = Lists.newArrayList(it);
//        if (CollectionUtils.isNotEmpty(esVideos)) {
//            List<Videos161Vo> videos = new ArrayList<Videos161Vo>();
//            for (FirstVideoEsVo firstVideoEsVo : esVideos) {
//                Videos161Vo videos161Vo = new Videos161Vo();
//                BeanUtils.copyProperties(firstVideoEsVo, videos161Vo);
//                videos.add(videos161Vo);
//            }
//            return videos;
//        }
//        return null;
//    }
//
//}
