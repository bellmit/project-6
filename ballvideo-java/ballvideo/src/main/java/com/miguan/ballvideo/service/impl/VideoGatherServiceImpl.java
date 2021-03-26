package com.miguan.ballvideo.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.EntityUtils;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.repositories.VideoEsRepository;
import com.miguan.ballvideo.repositories.VideoGatherJpaRepository;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @Author shixh
 * @Date 2020/1/10
 **/
@Service
@Log4j2
public class VideoGatherServiceImpl implements VideoGatherService {

    private int default_page = 8;

    private Long default_max_weight = 10000L;

    @Resource
    private VideoGatherJpaRepository videoGatherJpaRepository;

    @Resource
    private VideoEsRepository videoEsRepository;

    @Resource
    private VideoEsService videoEsService;

    @Resource
    private SysService sysService;

    @Resource
    private VideosCatMapper videosCatMapper;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private AdvertService advertService;

    @Resource
    private VideoCacheService videoCacheService;

    @Resource
    private ClUserService clUserService;

    @Resource
    private VideosCatService videosCatService;

    @Resource
    private DeviceVideoLogService deviceVideoLogService;

    @Resource
    private VideoAlbumMapper videoAlbumMapper;

    /**
     * 首页视频合集视频子集查询
     * @param gatherId
     * @param totalWeight
     * @return
     */
    @Override
    public List<Videos161Vo> getVideos(Long gatherId,Long totalWeight) {
        //根据当前权重查询后面的数据
        List<Videos161Vo> videos = getGatherVideosByPage(gatherId,totalWeight,"left",10000000L);
        //无数据或者数据少于8，继续获取后面的数据返回
        if(CollectionUtils.isEmpty(videos))return getGatherVideosByPage(gatherId,0L,"left",10000000L);
        if(videos.size()<default_page)videos.addAll(getGatherVideosByPage(gatherId,default_max_weight,"left",10000000L));
        return videos;
    }

    /**
     * 首页视频合集视频子集查询
     * @param gatherId
     * @param totalWeight
     * @return
     */
    public List<Videos161Vo> getVideoList(Long gatherId,Long totalWeight, Long videoId, String step) {
        //根据当前权重查询后面的数据
        List<Videos161Vo> videos = getGatherVideosByPage(gatherId,totalWeight,step,videoId);
        //无数据或者数据少于8，继续获取后面的数据返回
        if(CollectionUtils.isEmpty(videos))return getGatherVideosByPage(gatherId,0L,step,videoId);
        if(videos.size()<default_page)videos.addAll(getGatherVideosByPage(gatherId,default_max_weight,step,videoId));
        return videos;
    }

    @Override
    public Object getVideos(Long gatherId, CommonParamsVo param) {
        Map<String,Object> map = new HashMap<>();
        int currentPage  = param.getCurrentPage();
        currentPage-=1;//es从0开始
        if (currentPage < 0) {
            currentPage = 0;
        }
        Optional<VideoGather> videoGatherOpt = videoGatherJpaRepository.findById(gatherId);
        if(!videoGatherOpt.isPresent()){
            map.put("searchData","");
            map.put("page","");
            map.put("title","");
            return map;
        }
        VideoGather videoGather = videoGatherOpt.get();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.matchPhraseQuery("gatherId", gatherId));
        //屏蔽激励视频
        builder.must(matchQuery("incentiveVideo", "0"));
        //屏蔽不生效分类
        List<String> catIds = videosCatMapper.queryCatIdsList(param.getAppPackage());
        if (CollectionUtils.isNotEmpty(catIds)) {
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("catId",String.join(",", catIds).split(","));
            builder.mustNot(termsQueryBuilder);
        }
        Pageable pageable = PageRequest.of(currentPage, param.getPageSize(), Sort.Direction.DESC, "totalWeightLimit");
        Page<FirstVideoEsVo> pages = videoEsRepository.search(builder,pageable);
        List<FirstVideoEsVo> content = pages.getContent();
        List<Videos161Vo> videos161Vos = VideoUtils.packagingByEsVideos(content);
        //根据用户视频关联表判断是否收藏
        videoCacheService.getVideosCollection(videos161Vos, param.getUserId());
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos(videos161Vos);
        //根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
        VideoUtils.videoEncryption1(videos161Vos,videoAlbumVos,param.getMobileType(),param.getAppVersion());
        map.put("searchData", videos161Vos);
        map.put("page",pageable);
        map.put("title",videoGather.getTitle());
        return map;
    }

    @Override
    public Object getVideos(Long gatherId) {
        //videoEsRepository.deleteByGatherId(gatherId);
        VideoGather gather = this.getByGatherId(gatherId);
        if(gather==null){
            Map map = new HashMap();
            map.put("vo","");
            map.put("data",new ArrayList<FirstVideoEsVo>());
            return map;
        }
        VideoGatherVo vo = this.packing(gather);
        List<FirstVideoEsVo> esdatas = videoEsRepository.findByGatherId(vo.getId());
        Map map = new HashMap();
        map.put("vo",vo);
        map.put("data",esdatas);
        return map;
    }

    @Override
    public Object getVideos(String deviceId,Long gatherId, Long totalWeight, Long videoId, String step) {
        List<Videos161Vo> videos = getGatherVideosByPage(gatherId,totalWeight,step,videoId);
        if(videoId==null && CollectionUtils.isNotEmpty(videos)){
            videos.remove(0);
        }else if(CollectionUtils.isNotEmpty(videos)){
            for(Videos161Vo videos161Vo:videos){
                if(videos161Vo.getId().intValue()==videoId.intValue()){
                    //权重大小判断新增等于判断，要过滤自身
                    videos.remove(videos161Vo);break;
                }
            }
            //往右边滑动，权重排序由大到小
            if("right".equals(step)){
                videos = Lists.reverse(videos);
            }
        }
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos(videos);
        Map<String,Object> map = new HashMap<>();
        map.put("searchData",videos);
        return map;
    }

    /**
     * 前翻后翻获取视频数据
     * step-left,获取小于totalWeight的数据
     * step-right,获取大于totalWeight的数据
     * @param gatherId
     * @param totalWeight
     * @param step
     * @return
     */
    private List<Videos161Vo> getGatherVideosByPage(Long gatherId, Long totalWeight, String step, Long videoId) {
        totalWeight = VideoUtils.getTotalWeightLimit(totalWeight, videoId);
        QueryBuilder gteQb = QueryBuilders.rangeQuery("totalWeightLimit").lte(totalWeight);
        Sort.Direction direction =Sort.Direction.DESC;
        if("right".equals(step)){
            gteQb = QueryBuilders.rangeQuery("totalWeightLimit").gte(totalWeight);
            direction =Sort.Direction.ASC;
        }
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.matchPhraseQuery("gatherId", gatherId));
        builder.must(gteQb);
        //屏蔽激励视频
        builder.must(matchQuery("incentiveVideo", "0"));
        Pageable pageable = PageRequest.of(0, default_page,direction, "totalWeightLimit");
        List<FirstVideoEsVo> content = videoEsRepository.search(builder,pageable).getContent();
        List<Videos161Vo> videos = VideoUtils.packagingByEsVideos(content);
        return videos;
    }

    @Override
    public Object getDefaultVideos(AbTestAdvParamsVo queueVo, String userId, VideoGatherParamsDto params) {
        List<VideoGatherVo> videoGatherVos = getGatherVideos(userId,params);
        String appVersion = params.getAppVersion();
        boolean isHigh = VersionUtil.compareIsHigh(appVersion,Constant.APPVERSION_231);
        if(!isHigh){
            return videoGatherVos;
        }
        boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
        Map<String,Object> map = Maps.newHashMap();
        if (newFlag){
            Map<String, Object> paraMap = EntityUtils.entityToMap(params);
            List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, paraMap);
            map.put("advertCodeVos", advertCodeVos);
        }else {
            List<AdvertVo> advs = getAdvs(params);
            map.put("advs",advs);
        }
        map.put("videos",videoGatherVos);
        return map;
    }

    public List<AdvertVo> getAdvs(VideoGatherParamsDto params){
        boolean isShield = marketAuditService.isShield(params.getChannelId(),params.getAppVersion());
        if(isShield){
            return Lists.newArrayList();
        }
        Map<String, Object> map = EntityUtils.entityToMap(params);
        List<AdvertVo> advs = advertOldService.getBaseAdverts(map);
        if (CollectionUtils.isNotEmpty(advs)){
            advs = AdvUtils.computer(advs, advs.size());
        }
        return advs;
    }

    public List<VideoGatherVo> getGatherVideos(String userId,VideoGatherParamsDto params){
        //通过市场审核开关过滤屏蔽的合集
        List<VideoGather> gathers = getGathers(params);
        if(CollectionUtils.isEmpty(gathers))return null;
        List<VideoGatherVo> videoGatherVos = new ArrayList<VideoGatherVo>();
        for(VideoGather gather:gathers){
            VideoGatherVo vo = getByGather(gather,true);
            //根据用户视频关联表判断是否收藏
            videoCacheService.getVideosCollection(vo.getSearchData(), userId);
            //根据手机类型和版本号用视频加密数值替换bsyUrl值
            List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
            VideoUtils.videoEncryption1(vo.getSearchData(),videoAlbumVos,params.getMobileType(),params.getAppVersion());
            videoGatherVos.add(vo);
        }
        return videoGatherVos;
    }

    public VideoGatherVo packing(VideoGather gather){
        int count = this.countByGatherId(gather.getId());
        VideoGatherVo videoGatherVo = new VideoGatherVo();
        videoGatherVo.setId(gather.getId());
        videoGatherVo.setCount(count);
        videoGatherVo.setTitle(gather.getTitle());
        return videoGatherVo;
    }

    public VideoGatherVo packing(Long gatherId,String title){
        if(gatherId==null || StringUtils.isEmpty(title))return null;
        int count = this.countByGatherId(gatherId);
        VideoGatherVo videoGatherVo = new VideoGatherVo();
        videoGatherVo.setId(gatherId);
        videoGatherVo.setCount(count);
        videoGatherVo.setTitle(title);
        return videoGatherVo;
    }

    public VideoGatherVo getByGather(VideoGather gather,boolean includeSearchData) {
        VideoGatherVo videoGatherVo = packing(gather);
        if(includeSearchData){
            List<Videos161Vo> videos = getGatherVideosByPage(gather.getId(),default_max_weight,"left",10000000L);
            videoGatherVo.setSearchData(videos);
        }
        return videoGatherVo;
    }

    @Override
    public int countByGatherId(Long gatherId) {
        return videoEsRepository.countByGatherIdAndIncentiveVideo(gatherId, 0L);
    }

    @Override
    public VideoGather getByGatherId(Long gatherId) {
        Optional<VideoGather> optional = videoGatherJpaRepository.findById(gatherId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public VideoGatherVo getVideoGatherVoByGatherId(Long gatherId,boolean includeSearchData) {
        VideoGather gather = this.getByGatherId(gatherId);
        if(gather==null)return null;
        return this.getByGather(gather,includeSearchData);
    }

    /**
     * 根据当前视频获取同合集下其他视频，并且根据当前视频的权重往下排序
     * @param vo
     * @return
     */
    @Override
    public VideoGatherVo getVideoGatherVo(Videos161Vo vo) {
        VideoGatherVo videoGatherVo = packing(vo.getGatherId(),vo.getGatherTitle());
        if(videoGatherVo==null)return null;
        if(videoGatherVo.getCount()==0){
            videoGatherVo.setSearchData(Lists.newArrayList());
            return videoGatherVo;
        }
        List<Videos161Vo> gatherVideos = getVideos(vo.getGatherId(),vo.getTotalWeight());
        if(CollectionUtils.isEmpty(gatherVideos))gatherVideos = new ArrayList<Videos161Vo>();
        //大于或者小于权重排序，包含当前对象本身，需要去重复
        for(Videos161Vo v:gatherVideos){
            if(v.getId().intValue()==vo.getId().intValue()){
                gatherVideos.remove(v);break;
            }
        }
        Videos161Vo newObject = new Videos161Vo();
        BeanUtils.copyProperties(vo,newObject);
        gatherVideos.add(0,newObject);
        videoGatherVo.setSearchData(gatherVideos);
        return videoGatherVo;
    }

    /**
     * 根据当前视频获取同合集下其他视频，并且根据当前视频的权重往下排序
     * @param vo
     * @return
     */
    @Override
    public VideoGatherVo getCurrentVideoGatherVo(Videos161Vo vo) {
        VideoGatherVo videoGatherVo = packing(vo.getGatherId(),vo.getGatherTitle());
        if(videoGatherVo==null)return null;
        if(videoGatherVo.getCount()==0){
            videoGatherVo.setSearchData(Lists.newArrayList());
            return videoGatherVo;
        }
        List<Videos161Vo> gatherVideos = getVideoList(vo.getGatherId(),vo.getTotalWeight(),vo.getId(),"left");
        //视频作者头像和名字取用户表（虚拟用户或者真是用户）
        clUserService.packagingUserAndVideos(gatherVideos);
        if(CollectionUtils.isEmpty(gatherVideos))gatherVideos = new ArrayList<Videos161Vo>();
        //大于或者小于权重排序，包含当前对象本身，需要去重复
        for(Videos161Vo v:gatherVideos){
            if(v.getId().intValue()==vo.getId().intValue()){
                gatherVideos.remove(v);break;
            }
        }
        Videos161Vo newObject = new Videos161Vo();
        BeanUtils.copyProperties(vo,newObject);
        gatherVideos.add(0,newObject);
        videoGatherVo.setSearchData(gatherVideos);
        return videoGatherVo;
    }

    /** 通过市场审核开关过滤屏蔽的合集
     * @param params
     * @return
     */
    private List<VideoGather> getGathers(VideoGatherParamsDto params) {
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(params.getChannelId(),params.getAppVersion());
        String excludeIds = "";
        if(marketAudit!=null && StringUtils.isNotBlank(marketAudit.getGatherIds())){
            excludeIds = marketAudit.getGatherIds();
        }
        String[] split = excludeIds.split(",");
        return videosCatService.findGatherIdsNotIn(split, params.getCurrentPage(), params.getPageSize());
    }


    @Override
    public ResultMap refreshVideosByGatherId(Long gatherId) {
        videoEsService.deleteOrCloseGather(gatherId,0);
        videoEsService.updateByGatgherId(gatherId);
        sysService.delRedis("ballVideos:interface:newfirstVideo161:");
        return ResultMap.success();
    }

}
