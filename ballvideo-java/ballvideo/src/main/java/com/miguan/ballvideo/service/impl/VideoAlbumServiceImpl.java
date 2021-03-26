package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.EntityUtils;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.mapper.VideoAlbumMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.service.recommend.BloomFilterService;
import com.miguan.ballvideo.service.recommend.FindRecommendEsServiceImpl;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.VideoAlbumVo;
import com.miguan.ballvideo.vo.video.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频专辑
 **/
@Service
@Log4j2
public class VideoAlbumServiceImpl implements VideoAlbumService {

	@Resource
	private VideoAlbumMapper videoAlbumMapper;
	
	@Resource
	private ClUserService clUserService;

	@Resource
	private FirstVideosOldService firstVideosOldService;

    @Resource
    private AdvertService advertService;

	@Resource
	private RedisDB2Service redisDB2Service;

	@Resource
	private VideoCacheService videoCacheService;

	@Resource
	private FindRecommendEsServiceImpl findRecommendEsService;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private BloomFilterService bloomFilterService;

	@Resource
	private PredictService predictService;

	@Resource
	private MarketAuditService marketAuditService;

	private final static String code = "code";
	private final static int success = 200;

    @Override
	public List<VideoAlbumVo> getVideoAlbumList(VideoGatherParamsDto params) {
		PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
		List<VideoAlbumVo> videoAlbumList = videoAlbumMapper.findVideoAlbumList();
		// 权重相同的进行混排操作
		List<VideoAlbumVo> sortVideoAlbumList = new ArrayList<>();
		List<VideoAlbumVo> sortList = new ArrayList<>();
		Map<String, List<VideoAlbumVo>> vList = videoAlbumList.stream().collect(Collectors.groupingBy(e -> e.getSort().toString()));
		for (String key : vList.keySet()) {
			if (vList.get(key).size() > 1) {
				sortList = vList.get(key);
				Collections.shuffle(sortList);
				sortVideoAlbumList.addAll(sortList);
			} else {
				sortVideoAlbumList.add(vList.get(key).get(0));
			}
		}
		Collections.sort(sortVideoAlbumList, (o1, o2) -> o2.getSort().compareTo(o1.getSort()));
		return sortVideoAlbumList;
	}

    @Override
	public VideoAlbumListResultVo getVideoAlbumList26(AbTestAdvParamsVo queueVo, VideoGatherParamsDto params) {
		String faChannelId = ChannelUtil.filter(params.getChannelId(), params.getMobileType());
		String queryAlbumIds = StringUtil.getIdString(faChannelId, "collection_video_sort");
		PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
		List<VideoAlbumVo> sortVideoAlbumList = Lists.newArrayList();
		if (queryAlbumIds != "") {
			sortVideoAlbumList = videoAlbumMapper.findVideoAlbumListById(queryAlbumIds);
		} else {
			List<VideoAlbumVo> videoAlbumList = videoAlbumMapper.findVideoAlbumList();
			// 权重相同的进行混排操作
			List<VideoAlbumVo> sortList = new ArrayList<>();
			Map<String, List<VideoAlbumVo>> vList = videoAlbumList.stream().collect(Collectors.groupingBy(e -> e.getSort().toString()));
			for (String key : vList.keySet()) {
				if (vList.get(key).size() > 1) {
					sortList = vList.get(key);
					Collections.shuffle(sortList);
					sortVideoAlbumList.addAll(sortList);
				} else {
					sortVideoAlbumList.add(vList.get(key).get(0));
				}
			}
			Collections.sort(sortVideoAlbumList, (o1, o2) -> o2.getSort().compareTo(o1.getSort()));
		}
		Map<String, Object> map = EntityUtils.entityToMap(params);
		List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, map);
		List<VideosAlbumInfoVo>  videos = VideoUtils.packagingVideosInfo(advertCodeVos, sortVideoAlbumList);
		VideoAlbumListResultVo result = new VideoAlbumListResultVo();
		Pageable pageable = PageRequest.of(params.getCurrentPage(), params.getPageSize());
		result.setPage(pageable);
		result.setAlbumData(videos);
		return result;
	}

	//开关配置提取专辑Ids
	private String getAlbumIdString(String faChannelId, String name) {
		String searchCollectionSort = Global.getValue(name);
		String queryAlbumIds = "";
		if (searchCollectionSort.contains(faChannelId)) {
			List<String> channelInfoList = Arrays.asList(searchCollectionSort.split(";"));
			for (String channelInfo : channelInfoList) {
				if (channelInfo.contains(faChannelId)) {
					String[] str = channelInfo.split(":");
					queryAlbumIds = str[1];
					break;
				}
			}
		}
		return queryAlbumIds;
	}

	@Override
    public Page<Videos161Vo> getVideoAlbumDetailList(Long albumId, CommonParamsVo params) {
		List<Videos161Vo> videoAlbumDetailList;
		String userId = params.getUserId();
		if (userId == null || "0".equals(userId)) {
			PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
			videoAlbumDetailList = videoAlbumMapper.findVideoAlbumDetailList(albumId);
		}else {
			Map<String, Object> paraMap = new HashMap<>();
			paraMap.put("userId", userId);
			paraMap.put("videoAlbumId", albumId);
			PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
			videoAlbumDetailList = videoAlbumMapper.findVideoAlbumDetailListByUserId(paraMap);
		}
		//视频作者头像和名字取用户表（虚拟用户或者真是用户）
		clUserService.packagingUserAndVideos(videoAlbumDetailList);
		//根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
		VideoUtils.videoEncryption1(videoAlbumDetailList,videoAlbumVos,params.getMobileType(),params.getAppVersion());
		return (Page<Videos161Vo>) videoAlbumDetailList;
    }

	@Override
    public VideoAlbumResultVo getVideoAlbumDetailList(AbTestAdvParamsVo queueVo, Long albumId, CommonParamsVo params, String positionType, String permission) {
		List<Videos161Vo> videoAlbumDetailList;
		String userId = params.getUserId();
		if (userId == null || "0".equals(userId)) {
			PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
			videoAlbumDetailList = videoAlbumMapper.findVideoAlbumDetailList(albumId);
		}else {
			Map<String, Object> paraMap = new HashMap<>();
			paraMap.put("userId", userId);
			paraMap.put("videoAlbumId", albumId);
			PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
			videoAlbumDetailList = videoAlbumMapper.findVideoAlbumDetailListByUserId(paraMap);
		}
		//视频作者头像和名字取用户表（虚拟用户或者真是用户）
		clUserService.packagingUserAndVideos(videoAlbumDetailList);
		//根据手机类型和版本号用视频加密数值替换bsyUrl值
        List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
		VideoUtils.videoEncryption1(videoAlbumDetailList,videoAlbumVos,params.getMobileType(),params.getAppVersion());
		Map<String, Object> map = EntityUtils.entityToMap(params);
		map.put("positionType",positionType);
		map.put("permission",permission);
		List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, map);
		String incentiveVideoRate = "0";
		Map<String, Object> advParam = new HashMap<>();
		advParam.put("queryType","position");
		advParam.put("positionType", "incentiveVideoPosition");
		advParam.put("mobileType", params.getMobileType());
		advParam.put("appPackage", params.getAppPackage());
		List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(queueVo, advParam, 3);
		if (CollectionUtils.isNotEmpty(list)) {
			incentiveVideoRate = list.get(0).getSecondLoadPosition() == null ? "0" : list.get(0).getSecondLoadPosition() + "";
		}
		double rate = Double.parseDouble(incentiveVideoRate);
		for (Videos161Vo videos161Vo : videoAlbumDetailList) {
			if (videos161Vo.getIncentiveVideo() != null && videos161Vo.getIncentiveVideo() > 0) {
				videos161Vo.setIncentiveRate(rate);
			}
		}
		List<FirstVideosVo>  videos = VideoUtils.packagingAdvInfo(advertCodeVos, videoAlbumDetailList);
		VideoAlbumResultVo result = new VideoAlbumResultVo();
		Pageable pageable = PageRequest.of(params.getCurrentPage(), params.getPageSize());
		result.setPage(pageable);
		result.setSearchData(videos);
		return result;
    }

	@Override
	public FirstVideos firstVideosDetail(String userId, String id, Long albumId,String mobileType,String appVersion){
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("userId", userId);
		params.put("mobileType", mobileType);
		params.put("appVersion", appVersion);
		FirstVideos videosDetail = firstVideosOldService.findFirstVideosDetail(params);
		params.clear();
		params.put("firstVideosId", id);
		params.put("videoAlbumId", albumId);
		PackagingAlbumVo packagingAlbumVo = videoAlbumMapper.findVideoAlbumDetail(params);
		if (videosDetail != null && packagingAlbumVo != null){
			videosDetail.setAlbumTitle(packagingAlbumVo.getTitle());
			videosDetail.setNeedUnlock(packagingAlbumVo.getNeedUnlock());
		}
		return videosDetail;
	}

	/**
	 * 定时器每半小时执行一次，更新专辑视频Id到redis，
	 * 按权重倒叙排序，权重一样按视频Id倒叙排序
	 */
	@Scheduled(cron = "0 */30 * * * ?")
	private void flashVideoAlbumInfo() {
		RedisLock redisLock = new RedisLock(RedisKeyConstant.FLASH_VIDEO_ALBUM_INFO, RedisKeyConstant.defalut_seconds);
		if (redisLock.lock()) {
			log.info("---更新专辑视频Id开始---");
			updateAlbumDetailInfo("");
			log.info("---更新专辑视频Id结束---");
		}
	}

	//更新专辑对应视频ID列表信息
	private void updateAlbumDetailInfo(String queryAlbumId) {
		Map<String, Object> param = new HashMap<>();
		if (StringUtils.isNotEmpty(queryAlbumId)) {
			param.put("albumId", queryAlbumId);
		}
		List<VideoAlbumDetailVo> videoAlbumDetailVoList = videoAlbumMapper.findVideoAlbumDetailAll(param);
		if (CollectionUtils.isEmpty(videoAlbumDetailVoList)) {
			String keyDel = RedisKeyConstant.VIDEO_ALBUM_INFO + queryAlbumId;
			redisDB2Service.del(keyDel);
			return;
		}
		Map<Long, List<VideoAlbumDetailVo>> detailMap = videoAlbumDetailVoList.stream().collect(Collectors.groupingBy(VideoAlbumDetailVo::getVideoAlbumId));
		for (Map.Entry<Long, List<VideoAlbumDetailVo>> mapEntry : detailMap.entrySet()) {
			Long albumId = mapEntry.getKey();
			List<VideoAlbumDetailVo> albumDetailVos = mapEntry.getValue();
			List<Long> videoIdList = albumDetailVos.stream().map(VideoAlbumDetailVo::getFirstVideosId).collect(Collectors.toList());
			String videoIdStr = JSON.toJSONString(videoIdList);
			String key = RedisKeyConstant.VIDEO_ALBUM_INFO + albumId;
			redisDB2Service.set(key, videoIdStr,-1);
		}
		//删除禁用专辑视频信息
		String deleteAlbumIds = ",";
		List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
		Set<String> set = redisDB2Service.keys(RedisKeyConstant.VIDEO_ALBUM_INFO + "*");
		for (String key : set) {
			String[] keyStr = key.split(":");
			boolean deleteFlag = false;
			for (VideoAlbumVo albumVo : videoAlbumVos) {
				String albumId = albumVo.getId().toString();
				if (albumId.equals(keyStr[2])) {
					deleteFlag = true;
					break;
				}
			}
			if (!deleteFlag) {
				deleteAlbumIds = deleteAlbumIds + keyStr[2] + ",";
				redisDB2Service.del(key);
			}
		}
		if (deleteAlbumIds != ",") {
			String keyVideoId = RedisKeyConstant.VIDEO_ID_TO_ALBUM;
			Map<String, String> videoIdMap = redisDB2Service.hgetAll(keyVideoId);
			for (Map.Entry<String, String> entry : videoIdMap.entrySet()) {
				String videoId = entry.getKey();
				String albumId = "," + entry.getValue() + ",";
				if (deleteAlbumIds.contains(albumId)) {
					redisDB2Service.hdel(keyVideoId, videoId);
				}
			}
		}
	}

	@Override
	public Object getDefaultVideos(AbTestAdvParamsVo queueVo, String userId, VideoGatherParamsDto params) {
		List<VideoGatherVo> videoGatherVos = getAlbumVideos(userId,params);
		Map<String,Object> map = Maps.newHashMap();
		Map<String, Object> paraMap = EntityUtils.entityToMap(params);
		List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, paraMap);
		map.put("advertCodeVos", advertCodeVos);
		map.put("videos",videoGatherVos);
		return map;
	}

	private List<VideoGatherVo> getAlbumVideos(String userId,VideoGatherParamsDto params){
		//查询专辑信息
		List<VideoAlbumVo> albums = getAlbums(params);
		if(CollectionUtils.isEmpty(albums)) {
			return null;
		}
		List<VideoGatherVo> videoGatherVos = new ArrayList<>();
		for (VideoAlbumVo videoAlbumVo : albums) {
			VideoGatherVo vo = getByAlbum(videoAlbumVo, params.getAppPackage());
			if (vo != null) {
				//根据用户视频关联表判断是否收藏
				videoCacheService.getVideosCollection(vo.getSearchData(), userId);
				//根据手机类型和版本号用视频加密数值替换bsyUrl值
                List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
				VideoUtils.videoEncryption1(vo.getSearchData(), videoAlbumVos, params.getMobileType(), params.getAppVersion());
				videoGatherVos.add(vo);
			}
		}
		return videoGatherVos;
	}

	/** 通过市场审核开关过滤屏蔽的专辑
	 * @param params
	 * @return
	 */
	private List<VideoAlbumVo> getAlbums(VideoGatherParamsDto params) {
		//市场审核开关过滤屏蔽先不做
		/*MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(params.getChannelId(),params.getAppVersion());
		String excludeIds = "";
		if(marketAudit!=null && StringUtils.isNotBlank(marketAudit.getGatherIds())){
			excludeIds = marketAudit.getGatherIds();
		}
		String[] split = excludeIds.split(",");*/
		PageHelper.startPage(params.getCurrentPage(), params.getPageSize());
		Map<String,Object> map = new HashMap<>(1);
		String faChannelId = ChannelUtil.filter(params.getChannelId(), params.getMobileType());
		String queryAlbumIds = StringUtil.getIdString(faChannelId, "search_collection_sort");
		map.put("queryAlbumIds",queryAlbumIds);
		return videoAlbumMapper.findVideoAlbumAll(map);
	}

	/**
	 * 从redis取出专辑关联的视频Id，用视频Id查询ES视频详细信息
	 * @param videoAlbumVo
	 * @return
	 */
	private VideoGatherVo getByAlbum(VideoAlbumVo videoAlbumVo, String appPackage) {
		String key = RedisKeyConstant.VIDEO_ALBUM_INFO + videoAlbumVo.getId();
		String videoAlbumStr = redisDB2Service.get(key);
		if (StringUtils.isNotEmpty(videoAlbumStr)) {
			List<String> videoIdList = JSON.parseArray(videoAlbumStr, String.class);
			int count = videoIdList.size();
			VideoGatherVo videoGatherVo = new VideoGatherVo();
			videoGatherVo.setId(videoAlbumVo.getId());
			videoGatherVo.setCount(count);
			videoGatherVo.setTitle(videoAlbumVo.getTitle());
			int subSize = 8 > count ? count : 8;
			List<String> searchVideoList = videoIdList.subList(0, subSize);
			//根据视频id查询es
			double rate = getIncentiveVideoRate(appPackage);
			List<Videos161Vo> resultVideos = findRecommendEsService.list(searchVideoList, String.valueOf(rate));
			if (CollectionUtils.isEmpty(resultVideos)) {
				log.error("ES查询失败:getByAlbum,{}",JSON.toJSONString(videoAlbumVo));
			}
			videoGatherVo.setSearchData(resultVideos);
			return videoGatherVo;
		}
		return null;
	}

	@Override
	public VideoGatherVo getVideoAlbums(Long albumId, Long videoId, String step, String appPackage) {
		List<Videos161Vo> videos = Lists.newArrayList();
		String key = RedisKeyConstant.VIDEO_ALBUM_INFO + albumId;
		String videoAlbumStr = redisDB2Service.get(key);
		if (StringUtils.isNotEmpty(videoAlbumStr)) {
			List<String> videoIdList = JSON.parseArray(videoAlbumStr, String.class);
			int beginSize = 0;
			int endSize = 8;
			if (videoId != null) {
				for (int i = 0; i < videoIdList.size(); i++) {
					Long id = Long.valueOf(videoIdList.get(i));
					if (id.equals(videoId)) {
						int lastSize = videoIdList.size() - i - 1;
						if ("right".equals(step)) {
							beginSize = (i - 8) < 0 ? 0 : (i - 8);
							endSize = i;
						} else {
							beginSize = lastSize < 1 ? 0 : (i + 1) ;
							endSize = lastSize < 8 ? videoIdList.size() : i + 9;
						}
						break;
					}
				}
			}
			if (endSize != 0) {
				List<String> searchVideoList = videoIdList.subList(beginSize, endSize);
				//根据视频id查询es
				double rate = getIncentiveVideoRate(appPackage);
				videos = findRecommendEsService.list(searchVideoList, String.valueOf(rate));
				if (CollectionUtils.isNotEmpty(videos)) {
					//视频作者头像和名字取用户表（虚拟用户或者真是用户）
					clUserService.packagingUserAndVideos(videos);
				} else {
                    log.error("ES查询失败:getVideoAlbums,albumId:"+albumId+",videoId:"+videoId+",step:"+step);
                }
			}
		}
		VideoGatherVo resultVo = new VideoGatherVo();
		resultVo.setSearchData(videos);
		return resultVo;
	}

	private double getIncentiveVideoRate(String appPackage) {
					String incentiveVideoRate = "0";
					Map<String, Object> advParam = new HashMap<>();
					advParam.put("queryType", "position");
					advParam.put("positionType", "incentiveVideoPosition");
					advParam.put("mobileType", "2");
					advParam.put("appPackage", appPackage);
					List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(null, advParam, 3);
					if (CollectionUtils.isNotEmpty(list)) {
						incentiveVideoRate = list.get(0).getSecondLoadPosition() == null ? "0" : list.get(0).getSecondLoadPosition() + "";
					}
		return Double.parseDouble(incentiveVideoRate);
	}

	@Override
	public VideoGatherVo getCurrentVideoAlbums(Videos161Vo vo,String appPackage) {
		VideoGatherVo videoGatherVo = new VideoGatherVo();
		String albumId = "";
		if (vo.getAlbumId() != null) {
			albumId = vo.getAlbumId().toString();
		} else {
			String keyVideoId = RedisKeyConstant.VIDEO_ID_TO_ALBUM;
			albumId = redisDB2Service.hget(keyVideoId, vo.getId().toString());
		}
		if (StringUtils.isNotEmpty(albumId)) {
			VideoGatherVo albumVo = getVideoAlbums(Long.valueOf(albumId), vo.getId(), "left", appPackage);
			if (albumVo != null) {
				String key = RedisKeyConstant.VIDEO_ALBUM_INFO + albumId;
				String videoAlbumStr = redisDB2Service.get(key);
				if (StringUtils.isNotEmpty(videoAlbumStr)) {
					VideoAlbumVo albumInfo = this.getByAlbumId(Long.valueOf(albumId));
					List<String> videoIdList = JSON.parseArray(videoAlbumStr, String.class);
					int count = videoIdList.size();
					videoGatherVo.setId(albumInfo.getId());
					videoGatherVo.setCount(count);
					videoGatherVo.setTitle(albumInfo.getTitle());
                    List<Videos161Vo> videos = albumVo.getSearchData();
                    int maxSize = 7 > videos.size() ? videos.size() : 7;
                    List<Videos161Vo> newVideos = videos.subList(0, maxSize);
                    newVideos.add(0, vo);
                    videoGatherVo.setSearchData(newVideos);
                }
            }
		}
		return videoGatherVo;
	}

	@Override
	public void updateVideoIdAlbumChange(String albumId, String videoIds) {
		List<String> videoIdList = Arrays.asList(videoIds.split(","));
		for (String videoId : videoIdList) {
			String keyVideoId = RedisKeyConstant.VIDEO_ID_TO_ALBUM;
			String field = videoId;
			Long albumIdNew = videoAlbumMapper.findVideoAlbumByVideoId(Long.valueOf(videoId));
			if (albumIdNew == null || albumIdNew == 0L) {
				redisDB2Service.hdel(keyVideoId, field);
			} else {
				redisDB2Service.hset(keyVideoId, field, albumIdNew.toString());
			}
		}
		String json = VideoESOptions.albumIdUpdate.name() + RabbitMQConstant._MQ_ + videoIds;
		rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_REC_EXCHANGE,RabbitMQConstant.VIDEO_REC_KEY,json);
		updateAlbumDetailInfo(albumId);
	}

	@Override
	public void changeVideoIdAlbum() {
		String keyVideoId = RedisKeyConstant.VIDEO_ID_TO_ALBUM;
		redisDB2Service.del(keyVideoId);
		Map<Long, VideoAlbumDetailVo> resultMap = new HashMap<>();
		List<VideoAlbumDetailVo> detailVoList = videoAlbumMapper.findVideoAlbumDetailAll(new HashMap<>());
		for (VideoAlbumDetailVo detailVo : detailVoList) {
			Long videosId = detailVo.getFirstVideosId();
			Long sort = detailVo.getSort();
			VideoAlbumDetailVo oldDetailVo;
			Object object = MapUtils.getObject(resultMap, videosId);
			if (object != null) {
				oldDetailVo = (VideoAlbumDetailVo)object;
				if (sort > oldDetailVo.getSort()) {
					resultMap.put(videosId, detailVo);
				}
			} else {
				resultMap.put(videosId, detailVo);
			}
		}
		for (Map.Entry<Long, VideoAlbumDetailVo> map : resultMap.entrySet()) {
			redisDB2Service.hset(keyVideoId, map.getKey().toString(), map.getValue().getVideoAlbumId().toString());
		}
	}

	public VideoAlbumVo getByAlbumId(Long albumId) {
		VideoAlbumVo videoAlbumVo = videoAlbumMapper.findById(albumId);
		return videoAlbumVo;
	}

	@Override
	public VideoGatherVo getVideoAlbumVoByAlbumId(Long albumId,boolean includeSearchData) {
		VideoAlbumVo albumInfo = this.getByAlbumId(albumId);
		if (albumInfo == null) {
			return null;
		}
		return this.getByAlbum(albumInfo,includeSearchData);
	}

	public VideoGatherVo getByAlbum(VideoAlbumVo albumInfo,boolean includeSearchData) {
		VideoGatherVo videoGatherVo = new VideoGatherVo();
		String key = RedisKeyConstant.VIDEO_ALBUM_INFO + albumInfo.getId();
		String videoAlbumStr = redisDB2Service.get(key);
		if (StringUtils.isNotEmpty(videoAlbumStr)) {
			List<String> videoIdList = JSON.parseArray(videoAlbumStr, String.class);
			int count = videoIdList.size();
			videoGatherVo.setId(albumInfo.getId());
			videoGatherVo.setCount(count);
			videoGatherVo.setTitle(albumInfo.getTitle());
			if(includeSearchData) {
				int subSize = 8 > count ? count : 8;
				List<String> searchVideoList = videoIdList.subList(0, subSize);
				//根据视频id查询es
				List<Videos161Vo> resultVideos = findRecommendEsService.list(searchVideoList, "0");
				if (CollectionUtils.isEmpty(resultVideos)) {
                    log.error("ES查询失败:getByAlbum,{}",JSON.toJSONString(albumInfo));
                }
				videoGatherVo.setSearchData(resultVideos);
			}
		}
		return videoGatherVo;
	}

	@Override
	public List<Videos161Vo> getNextAlbumVideo(Long albumId, Long videoId, String publicInfo, String appPackage,String channelId,String appVersion,String abExp,String abIsol) {
		List<Videos161Vo> videos = Lists.newArrayList();
		if (albumId == null) {
			FirstVideoParamsVo paramsVo = new FirstVideoParamsVo();
            paramsVo.setPublicInfo(publicInfo);
			paramsVo.setVideoId(videoId.toString());
			paramsVo.setAbExp(abExp);
			paramsVo.setAbIsol(abIsol);
			MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
			if (marketAudit != null && marketAudit.getSensitiveState() == 1) {
				paramsVo.setSensitiveState("1");
			} else {
				paramsVo.setSensitiveState("0");
			}
			long pt = System.currentTimeMillis();
			String result = predictService.getRecommendVideoIds(paramsVo, Constant.recommend4);
			log.warn("大数据即时推荐耗时：{},推荐结果：{}", (System.currentTimeMillis() - pt), result);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)) {
				String data = jsonObject.getString("data");
				if (StringUtils.isNotEmpty(data)) {
					Map<String, Object> dataMap = JSONObject.parseObject(data, Map.class);
					Object videoObject = MapUtils.getObject(dataMap, "video");
					if (videoObject != null) {
						List<String> searchVideoList = (List<String>)videoObject;
						double rate = getIncentiveVideoRate(appPackage);
						videos = findRecommendEsService.list(searchVideoList, String.valueOf(rate));
						if (CollectionUtils.isNotEmpty(videos)) {
							clUserService.packagingUserAndVideos(videos);
							videos.get(0).setAlbumId(0L);
						}
						return videos;
					}
				}
			}
		}
		String key = RedisKeyConstant.VIDEO_ALBUM_INFO + albumId;
		String videoAlbumStr = redisDB2Service.get(key);
		if (StringUtils.isNotEmpty(videoAlbumStr)) {
			PublicInfo pbInfo  = new PublicInfo(publicInfo);
			List<String> videoIdList = JSON.parseArray(videoAlbumStr, String.class);
			List<String> searchVideoList = getNextVideoId(pbInfo.getUuid(), videoId.toString(), videoIdList);
			//根据视频id查询es
			if (CollectionUtils.isNotEmpty(searchVideoList)) {
				double rate = getIncentiveVideoRate(appPackage);
				videos = findRecommendEsService.list(searchVideoList, String.valueOf(rate));
				if (CollectionUtils.isNotEmpty(videos)) {
					//视频作者头像和名字取用户表（虚拟用户或者真是用户）
					clUserService.packagingUserAndVideos(videos);
					videos.get(0).setAlbumId(albumId);
					List<VideoAlbumVo> videoAlbumVos = videoAlbumMapper.findAlbumTitleByAll();
					for (VideoAlbumVo albumVo : videoAlbumVos) {
						if (albumVo.getId().equals(albumId)) {
							videos.get(0).setAlbumTitle(albumVo.getTitle());
							break;
						}
					}
				}
			}
		}
		return videos;
	}

	/**
	 * 获取下一个未曝光专辑视频Id
	 * @param uuid
	 * @param videoId 当前视频Id
	 * @param videoIdList 专辑全部视频Id
	 * @return
	 */
	private List<String> getNextVideoId(String uuid, String videoId, List<String> videoIdList) {
		List<String> searchVideoList = Lists.newArrayList();
		//videoId的位置
		int lastNum = videoIdList.size();
		if (lastNum < 2) {
			return searchVideoList;
		}
		//重新组装视频Id，当前videoId的videoIdList后半段放在前面，前半段放在后面
		List<String> newVideoList = Lists.newArrayList();
		List<String> halfVideoList = Lists.newArrayList();
		for (int i = 0; i < videoIdList.size(); i++) {
			String videoIdStr = videoIdList.get(i);
			if (videoIdStr.equals(videoId)) {
				lastNum = i;
			} else {
				if (i < lastNum) {
					//videoIdList前半段
					halfVideoList.add(videoIdStr);
				} else {
					//videoIdList后半段
					newVideoList.add(videoIdStr);
				}
			}
		}
		newVideoList.addAll(halfVideoList);
		//布隆过滤器按照newVideoList的顺序取数，取下一个未曝光视频Id
		List<String> bloomList = bloomFilterService.containMuil(1, uuid, newVideoList);
		if (CollectionUtils.isNotEmpty(bloomList)) {
			searchVideoList.add(bloomList.get(0));
		}
		return searchVideoList;
	}

}
