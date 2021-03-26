package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.EntityUtils;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.mapper.SearchHistoryLogMapper;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.repositories.VideoEsRepository;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.SearchHistoryLogVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.FirstVideosVo;
import com.miguan.ballvideo.vo.video.HotWordVideoVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.ballvideo.common.util.StringUtil.join;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @Author shixh
 * @Date 2020/1/7
 **/
@Slf4j
@Service
public class VideoEsServiceImpl implements VideoEsService {

    @Resource
    private ElasticsearchTemplate esTemplate;

    @Resource
    private VideoEsRepository videoEsRepository;

    @Resource
    private VideoCacheService videoCacheService;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private VideoGatherService videoGatherService;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private AdvertService advertService;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private ClUserService clUserService;

    @Resource
    private DeviceVideoLogService deviceVideoLogService;

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private HotWordService hotWordService;

    @Resource
    private SearchHistoryLogMapper searchHistoryLogMapper;

    @Resource
    private VideoAlbumService albumService;

    @Resource
    private VideoAlbumMapper videoAlbumMapper;

    private String video_sql = "select v.id,v.title,v.cat_id,v.url_img,v.bsy_url AS bsyUrl,v.created_at AS createdAt," +
            "UNIX_TIMESTAMP(v.created_at) as createDate,v.bsy_img_url AS bsyImgUrl,v.collection_count AS collectionCount," +
            "(v.love_count + v.love_count_real) AS loveCount,v.comment_count AS commentCount,v.is_incentive AS incentiveVideo," +
            "v.user_id,IFNULL(g.id, 0) AS gatherId,g.title as gatherTitle,v.bsy_m3u8,v.share_count shareCount, v.fake_share_count fakeShareCount," +
            "(v.watch_count + v.watch_count_real) AS watchCount,'0' collection,'0' love,v.bsy_head_url AS bsyHeadUrl," +
            "v.video_author AS videoAuthor,v.video_time,v.video_size AS videoSize,v.base_weight+v.real_weight as totalWeight," +
            "v.encryption_android_url,v.encryption_ios_url,v.encryption_xcx_url,v.state,v.videos_source,v.xcx_share_img"+
            " FROM first_videos v left join video_gather g ON g.id = v.gather_id AND g.state = 1 where v.state = 1 ";

    @Override
    public String getVideoSql(){
        return this.video_sql;
    }

    @Override
    public ResultMap deleteIndex(String index) {
        if (esTemplate.indexExists(index)) {
            return ResultMap.success(esTemplate.deleteIndex(index));
        }
        return ResultMap.error("索引不存在，删除失败");
    }

    @Override
    public Object init() {
        if (!esTemplate.indexExists(FirstVideoEsVo.class)) {
            esTemplate.createIndex(FirstVideoEsVo.class);
            esTemplate.putMapping(FirstVideoEsVo.class);
        } else {
            //esTemplate.deleteIndex(FirstVideoEsVo.class);
        }
        int day = Global.getInt("init_video_mostRecentDays");
        String countSQL = "select count(*) from first_videos where state = 1 and (TO_DAYS(NOW()) - TO_DAYS(created_at)) <= " + day;
        Object o = dynamicQuery.nativeQueryObject(countSQL);
        if (o == null){
            return ResultMap.error("无可操作数据");
        }
        int count = Integer.parseInt(o + "");
        int loop = 0;
        while (true) {
            String append = " and (TO_DAYS(NOW()) - TO_DAYS(v.created_at)) <= " + day + " order by v.created_at desc limit " + loop + "," + 5000;
            String json = VideoESOptions.initVideo.name() + RabbitMQConstant._MQ_ + append;
            rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
            if (loop >= count) {
                break;
            }
            loop += 5000;
        }
        log.info("es首页视频初始化开始。。。");
        return ResultMap.success();
    }

    @Override
    public void init(String sqlBuffer) {
        String sql = video_sql + sqlBuffer;
        List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
        log.info("初始化视频数据：" + sqlBuffer.split("limit")[1] + "...");
        if (CollectionUtils.isNotEmpty(videos)) {
            saveToEs(videos);
        }
    }

    @Override
    public void saveToEs(List<Videos161Vo> videos) {
        try {
            videoCacheService.fillParams(videos);
            List<FirstVideoEsVo> csVideos = new ArrayList<FirstVideoEsVo>();
            for (Videos161Vo video : videos) {
                FirstVideoEsVo csVideo = new FirstVideoEsVo();
                BeanUtils.copyProperties(video, csVideo);
                csVideo.setTotalWeightLimit(VideoUtils.getTotalWeightLimit(csVideo.getTotalWeight(),csVideo.getId()));
                csVideos.add(csVideo);
            }
            videoEsRepository.saveAll(csVideos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public FirstVideoEsVo getById(Long id) {
        Optional<FirstVideoEsVo> optional = videoEsRepository.findById(id);
        if (optional.isPresent()) {
            FirstVideoEsVo vo = optional.get();
            if (vo.getGatherId() > 0 && StringUtils.isEmpty(vo.getGatherTitle())) {
                //存在合集不为空但是标题为空的问题，临时处理
                update(vo.getId() + "", VideoESOptions.videoUpdate.name());
                return getById(id);
            }
            return vo;
        }
        return null;
    }

    @Override
    public Object getByCatId(Long id,Long incentiveVideo) {
        List<FirstVideoEsVo> esdatas = videoEsRepository.findByCatIdAndIncentiveVideo(id, incentiveVideo);
        Map map = new HashMap();
        map.put("data",esdatas);
        return map;
    }

    @Override
    public void save(FirstVideoEsVo vo) {
        vo.setTotalWeightLimit(VideoUtils.getTotalWeightLimit(vo.getTotalWeight(),vo.getId()));
        videoEsRepository.save(vo);
    }

    @Override
    public Object updateByGatgherId(Long gatherId) {
        String sql = video_sql + " and v.gather_id =" + gatherId;
        List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
        if (CollectionUtils.isNotEmpty(videos)) {
            saveToEs(videos);
        }
        return ResultMap.success();
    }

    @Override
    public Object getMyGatherVidesoById(Long videoId) {
        String sql = video_sql + " and v.id = " + videoId;
        List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
        if (CollectionUtils.isNotEmpty(videos)) {
            Videos161Vo videos161Vo = videos.get(0);
            return videoGatherService.getVideoGatherVo(videos161Vo);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object search(String title, String userId, VideoGatherParamsDto param, AbTestAdvParamsVo queueVo) {
        Long userIdL = userId == null ? 0L : Long.valueOf(userId);
        SearchHistoryLogVo logVo = new SearchHistoryLogVo();
        logVo.setDeviceId(param.getDeviceId());
        logVo.setSearchContent(title);
        logVo.setUserId(userIdL);
        String dataStr = JSON.toJSONString(logVo);
        rabbitTemplate.convertAndSend(RabbitMQConstant.SEARCH_HISTORY_EXCHANGE, RabbitMQConstant.SEARCH_HISTORY_KEY, dataStr);
        String appVersion = param.getAppVersion();
        boolean isHigh = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_231);
        int currentPage = param.getCurrentPage() - 1;//es从0开始
        if (currentPage < 0) {
            currentPage = 0;
        }
        Pageable pageable = PageRequest.of(currentPage, param.getPageSize());
        List<Videos161Vo> esVideos = getVideoInfoList(pageable, title, param);
        //根据用户视频关联表判断是否收藏
        videoCacheService.getVideosCollection(esVideos, userId);
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos(esVideos);
        //根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
        VideoUtils.videoEncryption1(esVideos,videoAlbumVos,param.getMobileType(),appVersion);

        if (!isHigh) {
            Map<String, Object> map = new HashMap<>();
            map.put("searchData", esVideos);
            map.put("page", pageable);
            return map;
        }
        boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
        List<FirstVideosVo> videos;
        if (newFlag) {
            //V2.5.0
            Map<String, Object> map = EntityUtils.entityToMap(param);
            List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, map);
            videos = VideoUtils.packagingNewBySearch(advertCodeVos, esVideos);
        } else {
            List<AdvertVo> advs = getAdvs(param);
            videos = VideoUtils.packagingBySearch(advs, esVideos);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("searchData", videos);
        map.put("page", pageable);
        return map;
    }

    public List<AdvertVo> getAdvs(VideoGatherParamsDto params) {
        Map<String, Object> map = EntityUtils.entityToMap(params);
        boolean flag = toolMofangService.stoppedByMofang(map);
        if (flag){
            return Lists.newArrayList();
        }
        List<AdvertVo> advs = advertOldService.getBaseAdverts(map);
        if (CollectionUtils.isNotEmpty(advs)) {
            advs = AdvUtils.computer(advs, advs.size());
        }
        return advs;
    }

    public List<Videos161Vo> getVideos(Pageable pageable, String title, VideoGatherParamsDto param, String ids) {
        String preTag = "<font color='#FF5765'>";
        String postTag = "</font>";
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(param.getChannelId(), param.getAppVersion());
        //title
        MatchQueryBuilder matchQueryBuilder = matchQuery("title", title);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //catid
        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getCatIds())) {
            TermsQueryBuilder builder = QueryBuilders.termsQuery("catId", marketAudit.getCatIds().split(","));
            queryBuilder.mustNot(builder);
        }
        //V2.5.6 屏蔽合集
        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getGatherIds())) {
            TermsQueryBuilder builder = QueryBuilders.termsQuery("gatherId", marketAudit.getGatherIds().split(","));
            queryBuilder.mustNot(builder);
        }
        //屏蔽激励视频
        queryBuilder.must(matchQuery("incentiveVideo", "0"));
        if (ids != null) {
            TermsQueryBuilder builder = QueryBuilders.termsQuery("id", ids.split(","));
            queryBuilder.mustNot(builder);
        }
        queryBuilder.must(matchQueryBuilder);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag)).build();
        searchQuery.setPageable(pageable);


        AggregatedPage<FirstVideoEsVo> items = esTemplate.queryForPage(searchQuery, FirstVideoEsVo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Videos161Vo> chunk = new ArrayList<Videos161Vo>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Long id = Long.parseLong(searchHit.getId());
                    FirstVideoEsVo firstVideoEsItem = getById(id);
                    HighlightField highlightField = searchHit.getHighlightFields().get("title");
                    if (highlightField != null) {
                        firstVideoEsItem.setColorTitle(highlightField.fragments()[0].toString());
                        Videos161Vo videos161Vo = new Videos161Vo();
                        BeanUtils.copyProperties(firstVideoEsItem, videos161Vo);
                        if (videos161Vo.getGatherId() > 0) {
                            VideoGatherVo videoGatherVo = videoGatherService.getVideoGatherVoByGatherId(videos161Vo.getGatherId(), false);
                            videos161Vo.setVideoGatherVo(videoGatherVo);
                        }
                        chunk.add(videos161Vo);
                    }
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }
        });
        if (items == null){
            return new ArrayList<Videos161Vo>();
        }
        PageImpl<Videos161Vo> page = new PageImpl(items.getContent());
        return items == null ? new ArrayList<Videos161Vo>() : page.getContent();
    }

    /** 这里传参数 videoAdd 针对es做如下操作
     *  如果mysql中存在，es中不存在，从 mysql 读取并新增
     *  如果es中已经存在，那么从 mysql 读取并更新*/
    @Override
    public Object update(String videoIds, String options) {
        if (VideoESOptions.videoAdd.name().equals(options) || VideoESOptions.directVideoAdd.name().equals(options)) {
            String sql = video_sql + " and v.id in(" + videoIds + ")";
            List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
            if (CollectionUtils.isNotEmpty(videos)) {
                saveToEs(videos);  //更新es
            }
        } else if (VideoESOptions.videoDelete.name().equals(options)) {
            String[] ids = videoIds.split(",");
            for (String id : ids) {
                long videoId = Long.parseLong(id);
                videoEsRepository.deleteById(videoId);
            }
        } else if (VideoESOptions.videoUpdate.name().equals(options)) {
            String sql = video_sql + " and v.id in(" + videoIds + ")";
            List<Videos161Vo> videos = dynamicQuery.nativeQueryList(Videos161Vo.class, sql);
            if (CollectionUtils.isNotEmpty(videos)) {
                saveToEs(videos);  //更新es
            }
        }
        return null;
    }

    @Override
    public Object updateByGatherId(Long gatherId, String videoIds) {
        List<String> vodeoIds_arrA = Arrays.asList(videoIds.split(","));
        for (String strId : vodeoIds_arrA) {
            Long id = Long.parseLong(strId);
            FirstVideoEsVo firstVideoEsVo = getById(id);
            if (firstVideoEsVo != null) {
                firstVideoEsVo.setGatherId(gatherId);
                if (gatherId > 0) {
                    VideoGather gather = videoGatherService.getByGatherId(gatherId);
                    if (gather != null) {
                        firstVideoEsVo.setGatherTitle(gather.getTitle());
                    } else {
                        firstVideoEsVo.setGatherTitle("");
                        firstVideoEsVo.setGatherId(0L);
                    }
                }
                videoEsRepository.save(firstVideoEsVo);
            }
        }
        return ResultMap.success();
    }

    @Override
    public void deleteOrCloseGather(long gatherId, int state) {
        //state:0-关闭,1-删除
        List<FirstVideoEsVo> videos = videoEsRepository.findByGatherId(gatherId);
        if (CollectionUtils.isNotEmpty(videos)) {
            for (FirstVideoEsVo video : videos) {
                if (state == 0 || state == 1) {
                    video.setGatherId(0L);
                    video.setGatherTitle("");
                }
            }
            videoEsRepository.saveAll(videos);
        }
    }

    @Override
    public void deleteDueVideos() {
        int day = Global.getInt("init_video_mostRecentDays");
        long seconds = System.currentTimeMillis()/1000-60*60*24*day;
        List<FirstVideoEsVo> videos = this.videoEsRepository.findByCreateDateLessThanEqual(seconds);
        if(CollectionUtils.isNotEmpty(videos)){
            videoEsRepository.deleteAll(videos);
        }
    }

    @Override
    public List<Videos161Vo> query(Map<String, Object> params) {
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();        //builder下有must、should以及mustNot 相当于sql中的and、or以及not
        String catId = MapUtils.getString(params, "catId");
        if (StringUtils.isNotEmpty(catId) && !"0".equals(catId) && !"created3Day".equals(catId)) {
            builder.must(matchQuery("catId", params.get("catId") + ""));
        }
        String incentive = MapUtils.getString(params, "incentive");
        if (StringUtils.isNotEmpty(incentive)) {
            builder.must(matchQuery("incentiveVideo", params.get("incentive") + ""));
        }
        String otherCatIds = MapUtils.getString(params, "otherCatIds");
        if (StringUtils.isNotEmpty(otherCatIds)) {
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("catId", params.get("otherCatIds").toString().split(","));
            builder.mustNot(termsQueryBuilder);
        }
        String showedIds = MapUtils.getString(params, "showedIds");
        if (StringUtils.isNotEmpty(showedIds) && !"null".equals(showedIds)) {
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("id", params.get("showedIds").toString().split(","));
            builder.mustNot(termsQueryBuilder);
        }
        //V2.5.6ES屏蔽集合
        String gatherIds = MapUtils.getString(params, "gatherIds");
        if (StringUtils.isNotEmpty(gatherIds) && !"null".equals(gatherIds)) {
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("gatherId", params.get("gatherIds").toString().split(","));
            builder.mustNot(termsQueryBuilder);
        }
        builder.must(matchQuery("gatherId", 0));   //屏蔽全部集合视频
        if ("created3Day".equals(catId)) {
            FieldSortBuilder sort = SortBuilders.fieldSort("createDate").order(SortOrder.DESC);
            nativeSearchQueryBuilder.withSort(sort);
        }
        if (StringUtils.isNotEmpty(catId) && !"created3Day".equals(catId)) {
            FieldSortBuilder sort = SortBuilders.fieldSort("totalWeight").order(SortOrder.DESC);
            nativeSearchQueryBuilder.withSort(sort);
        }
        String numStr = MapUtils.getString(params, "num");
        int num = StringUtils.isEmpty(numStr) ? 0 : Integer.parseInt(numStr);
        if (num > 0) {
            pageable = PageRequest.of(0, num);
        }
        String queryNumber = MapUtils.getString(params, "queryNumber");
        //queryNumber随机标识
        if (StringUtils.isNotEmpty(queryNumber)) {
            Script script = new Script("Math.random()");
            ScriptSortBuilder scriptSortBuilder =
                    SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
            nativeSearchQueryBuilder.withQuery(builder).withSort(scriptSortBuilder);
        } else {
            nativeSearchQueryBuilder.withQuery(builder);
        }
        nativeSearchQueryBuilder.withPageable(pageable);
        return getVideos161Vos(nativeSearchQueryBuilder);
    }

    @Override
    public List<Videos161Vo> query(Map<String, Object> params, int num) {
        Script script = new Script("Math.random()");
        ScriptSortBuilder scriptSortBuilder =
                SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
        String catId = MapUtils.getString(params, "catId");
        Pageable pageable = PageRequest.of(0, num);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(catId)) {
            builder.must(matchQuery("catId", catId));
        }
        String otherCatIds = MapUtils.getString(params, "otherCatIds");
        if (StringUtils.isNotEmpty(otherCatIds)) {
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("catId", params.get("otherCatIds").toString().split(","));
            builder.mustNot(termsQueryBuilder);
        }
        //屏蔽激励视频
        builder.must(matchQuery("incentiveVideo", "0"));
        //V2.5.6ES屏蔽集合
        String channelId = MapUtils.getString(params, "channelId");
        String appVersion = MapUtils.getString(params, "appVersion");
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
        if (marketAudit != null && StringUtils.isNotBlank(marketAudit.getGatherIds())) {
            String gatherIds = marketAudit.getGatherIds();
            if (StringUtils.isNotEmpty(gatherIds) && !"null".equals(gatherIds)) {
                TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("gatherId", params.get("gatherIds").toString().split(","));
                builder.mustNot(termsQueryBuilder);
            }
        }
        builder.must(matchQuery("gatherId", 0));  //屏蔽全部合集视频
        nativeSearchQueryBuilder.withQuery(builder).withSort(scriptSortBuilder);
        nativeSearchQueryBuilder.withPageable(pageable);
        return getVideos161Vos(nativeSearchQueryBuilder);
    }

    @Override
    public List<Videos161Vo> queryByIds(List<String> videos) {
        List<Long> videoIds = videos.stream().map(Long::valueOf).collect(Collectors.toList());
        TermsQueryBuilder termsQuery = new TermsQueryBuilder("id", videoIds);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(termsQuery);
        List<FirstVideoEsVo> list = esTemplate.queryForList(nativeSearchQuery, FirstVideoEsVo.class);
        if (CollectionUtils.isEmpty(list)) {
            //log.error("从es中未找到任何的视频数据，ids = {}", JSON.toJSONString(videos));
            return null;
        }
        return list.stream().sorted(Comparator.comparingLong(e->videoIds.indexOf(e.getId()))).map(e -> {
            Videos161Vo videos161Vo = new Videos161Vo();
            BeanUtils.copyProperties(e, videos161Vo);
            return videos161Vo;
        }).collect(Collectors.toList());
    }


    private List<Videos161Vo> getVideoInfoList(Pageable pageable, String title, VideoGatherParamsDto param) {
        List<Videos161Vo> resultList;
        //查询热词关联的视频列表
        List<HotWordVideoVo> hotWordVideoList = hotWordService.findHotWordVideoInfo(title);
        if (CollectionUtils.isNotEmpty(hotWordVideoList)) {
            String ids = hotWordVideoList.stream().map(h -> h.getVideoId() + "" ).collect(Collectors.joining(","));
            resultList = getVideoList(hotWordVideoList.size(), ids, param);
            if (CollectionUtils.isNotEmpty(resultList)) {
                int currentPage = param.getCurrentPage() - 1;//es从0开始
                if (currentPage < 0) {
                    currentPage = 0;
                }
                int modNum = resultList.size() % param.getPageSize();
                int pageNum = 0;
                if (modNum != 0) {
                    pageNum = currentPage - (resultList.size() / param.getPageSize() + 1);
                } else {
                    pageNum = currentPage - resultList.size() / param.getPageSize();
                }
                if (pageNum < 0) {
                    int sizeBegin = currentPage * param.getPageSize();
                    int sizeEnd = sizeBegin + param.getPageSize();
                    if (sizeEnd > resultList.size()) {
                        sizeEnd = resultList.size();
                    }
                    Map<Long, Integer> hotWordVideoMap = hotWordVideoList.stream().collect(Collectors.toMap(HotWordVideoVo::getVideoId, HotWordVideoVo::getSort));
                    for (Videos161Vo videos161Vo : resultList) {
                        videos161Vo.setColorTitle(videos161Vo.getTitle());
                        videos161Vo.setSort(hotWordVideoMap.get(videos161Vo.getId()));
                    }
                    String sortType = hotWordVideoList.get(0).getVideoSort();
                    if ("index".equals(sortType)) {
                        resultList.sort(Comparator.comparing(Videos161Vo::getSort));
                    } else {
                        resultList.sort(Comparator.comparing(Videos161Vo::getTotalWeight).reversed().thenComparing(Videos161Vo::getId));
                    }
                    resultList = resultList.subList(sizeBegin, sizeEnd);
                    for (Videos161Vo videos161Vo : resultList) {
                        if (videos161Vo.getGatherId() > 0) {
                            String appVersion = param.getAppVersion();
                            boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_322);
                            if (newFlag) {
                                VideoGatherVo videoGatherVo = albumService.getVideoAlbumVoByAlbumId(videos161Vo.getGatherId(), false);
                                videos161Vo.setVideoGatherVo(videoGatherVo);
                            } else {
                                VideoGatherVo videoGatherVo = videoGatherService.getVideoGatherVoByGatherId(videos161Vo.getGatherId(), false);
                                videos161Vo.setVideoGatherVo(videoGatherVo);
                            }
                        }
                    }
                    //不够10个视频补全
                    int listSize = 10 - resultList.size();
                    if (listSize > 0) {
                        Pageable pageableSearch = PageRequest.of(0, listSize);
                        List<Videos161Vo> lastList = getVideos(pageableSearch, title, param, ids);
                        resultList.addAll(lastList);
                    }
                } else {
                    //最后一页不够10条，屏蔽补充的视频
                    int listSize = 10 - modNum;
                    if (listSize > 0) {
                        Pageable pageableSearch = PageRequest.of(0, listSize);
                        List<Videos161Vo> lastList = getVideos(pageableSearch, title, param, ids);
                        String lastIds = lastList.stream().map(h -> h.getId() + "" ).collect(Collectors.joining(","));
                        ids = ids + "," + lastIds;
                    }
                    Pageable pageableSearch = PageRequest.of(pageNum, param.getPageSize());
                    resultList = getVideos(pageableSearch, title, param, ids);
                }
            } else {
                resultList = getVideos(pageable, title, param, null);
            }
        } else {
            resultList = getVideos(pageable, title, param, null);
        }
        return resultList;
    }

    //根据视频Id查询数据
    @Cacheable(value = CacheConstant.QUERY_HOT_WORD_VIDEO)
    public List<Videos161Vo> getVideoList(int num, String ids, VideoGatherParamsDto param) {
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(param.getChannelId(), param.getAppVersion());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //catid
        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getCatIds())) {
            TermsQueryBuilder builder = QueryBuilders.termsQuery("catId", marketAudit.getCatIds().split(","));
            queryBuilder.mustNot(builder);
        }
        //V2.5.6 屏蔽合集
        if (marketAudit != null && StringUtils.isNotEmpty(marketAudit.getGatherIds())) {
            TermsQueryBuilder builder = QueryBuilders.termsQuery("gatherId", marketAudit.getGatherIds().split(","));
            queryBuilder.mustNot(builder);
        }
        //屏蔽激励视频
        queryBuilder.must(matchQuery("incentiveVideo", "0"));
        //视频Id
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("id", ids.split(","));
        queryBuilder.must(termsQueryBuilder);
        Pageable pageable = PageRequest.of(0, num);
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        searchQuery.withQuery(queryBuilder);
        searchQuery.withPageable(pageable);
        return getVideos161Vos(searchQuery);
    }

    //根据视频Id查询数据
    @Override
    public List<Videos161Vo> getVideoByIdList(String ids) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("id", ids.split(","));
        queryBuilder.must(termsQueryBuilder);
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        searchQuery.withQuery(queryBuilder);
        return getVideos161Vos(searchQuery);
    }

    private List<Videos161Vo> getVideos161Vos(NativeSearchQueryBuilder searchQuery) {
        Iterable it = this.videoEsRepository.search(searchQuery.build());
        List<FirstVideoEsVo> esVideos = Lists.newArrayList(it);
        if (CollectionUtils.isNotEmpty(esVideos)) {
            List<Videos161Vo> videos = new ArrayList<>();
            for (FirstVideoEsVo firstVideoEsVo : esVideos) {
                Videos161Vo videos161Vo = new Videos161Vo();
                BeanUtils.copyProperties(firstVideoEsVo, videos161Vo);
                videos.add(videos161Vo);
            }
            return videos;
        }
        return null;
    }

    @Override
    public void videoDataDeleteMQ(String type) {
        if ("1".equals(type)) {
            List<String> firstVideoIdsOne = firstVideosMapper.findFirstVideoIdsOne();
            if (CollectionUtils.isNotEmpty(firstVideoIdsOne)) {
                log.info("少于20秒视频数据共"+ firstVideoIdsOne.size()+"条");
                List<List<String>> partition = Lists.partition(firstVideoIdsOne, 500);
                for (int i = 0; i < partition.size(); i++) {
                    List<String> list = partition.get(i);
                    String videoIds = join(list, ",");
                    String json = "videoDelete" + RabbitMQConstant._MQ_ + videoIds;
                    rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
                }
            }
        } else if ("2".equals(type)) {
            List<String> firstVideoIdsTwo = firstVideosMapper.findFirstVideoIdsTwo();
            if (CollectionUtils.isNotEmpty(firstVideoIdsTwo)) {
                log.info("少于5个汉字标题的数据共"+ firstVideoIdsTwo.size()+"条");
                List<List<String>> partition = Lists.partition(firstVideoIdsTwo, 500);
                for (int i = 0; i < partition.size(); i++) {
                    List<String> list = partition.get(i);
                    String videoIds = join(list, ",");
                    String json = "videoDelete" + RabbitMQConstant._MQ_ + videoIds;
                    rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
                }
            }
        }
    }

    @Override
    public void saveSearchInfo(SearchHistoryLogVo logVo) {
        Integer num = searchHistoryLogMapper.findSearchInfo(logVo);
        if (num == null || num == 0) {
            searchHistoryLogMapper.saveSearchInfo(logVo);
        }
    }
}
