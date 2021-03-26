package com.miguan.ballvideo.service.impl;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.entity.VideoExposureOneHourCount;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.IPUtils;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.VideoExposureOneHourCountDao;
import com.miguan.ballvideo.service.RedisDB3Service;
import com.miguan.ballvideo.service.VideoExposureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 曝光视频统计
 * @author laiyd
 * @date 2020-06-05
 **/
@Slf4j
@Service
public class VideoExposureServiceImpl implements VideoExposureService {

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private RedisDB3Service redisDB3Service;

	@Resource
	private VideoExposureOneHourCountDao videoExposureOneHourCountDao;

	/**
	 * 曝光视频记录，发送到MQ
	 * @param deviceId 设备Id
	 * @param videoId 视频信息
	 * @return
	 */
	@Override
	public boolean videoExposureCountSendToMQ(String deviceId, String videoId) {
		String videoExposureSwitch = Global.getValue("video_exposure_switch");
		if (Constant.OPENSTR.equals(videoExposureSwitch) && StringUtils.isNotEmpty(videoId) && StringUtils.isNotEmpty(deviceId)) {
			String json = deviceId + "@" + videoId;
			rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_EXPOSURE_COUNT_EXCHANGE, RabbitMQConstant.VIDEO_EXPOSURE_COUNT_KEY, json);
		}
		return true;
	}

	/**
	 * 曝光视频记录，保存到redis(临时存储)
	 * @param jsonMsg 待待统计的曝光视频信息
	 * @return
	 */
	@Override
	public void videoExposureSendToRedis(String jsonMsg) {
		if (StringUtils.isEmpty(jsonMsg)) return;
        String ip = IPUtils.getHostAddress();
        String key = RedisKeyConstant.VIDEO_EXPOSURE_DATA + ip;
        Long lpush = redisDB3Service.lpush(key, jsonMsg);
        Integer videoExposureLimit = Global.getInt("video_exposure_limit");
        if (lpush >= videoExposureLimit) {
            List<String> lrange = redisDB3Service.lrange(key, 0, -1);
            redisDB3Service.del(key);
            if (CollectionUtils.isEmpty(lrange)) return;
            videoExposureCountInfo(lrange);
        }
	}

	/**
	 * 曝光视频记录，进行处理统计
	 * @param list 待待统计的曝光视频信息
	 * @return
	 */
	@Override
	public void videoExposureCountInfo(List<String> list) {
		String ip = IPUtils.getHostAddress();
		for (String str : list) {
			//1小时曝光数
			String key2 = RedisKeyConstant.VIDEO_EXPOSURE_ONE_HOUR + str;
			boolean exitsData2 = redisDB3Service.exits(key2);
			if (!exitsData2) {
				String[] ss = str.split("@");
				String addKey = RedisKeyConstant.VIDEO_EXPOSURE_ONE_HOUR_DATA + ip;
				Long lpush = redisDB3Service.lpush(addKey, ss[1]);
				Integer videoExposureOneHourLimit = Global.getInt("video_exposure_one_hour_limit");
				if (lpush >= videoExposureOneHourLimit) {
					List<String> lrange = redisDB3Service.lrange(addKey, 0, -1);
					redisDB3Service.del(addKey);
					if (CollectionUtils.isEmpty(lrange)) return;
					updateVideoExposureInfo(lrange);
				}
				redisDB3Service.set(key2, 1,RedisKeyConstant.VIDEO_EXPOSURE_ONE_HOUR_SECONDS);
			}
		}
	}

	/**
	 * 视频曝光数
	 * @param lrange
	 */
	private void updateVideoExposureInfo(List<String> lrange) {
		Map<Long, List<Long>> addMap = VideoUtils.getLongListMap(lrange);
		for(Map.Entry<Long, List<Long>> mapEntry : addMap.entrySet()) {
			Long mapKey = mapEntry.getKey();
			List<Long> mapValue = mapEntry.getValue();
            //1小时曝光数
            updateVideoExposureOneHourCountBatch(mapValue, mapKey);
		}
	}

	/**
	 * 更新视频曝光总数信息
	 * @param ids
	 */
	/*@Transactional
	public void updateVideoExposureTotalCountBatch(List<Long> ids, Long updateNum) {
		List<VideoExposureTotalCount> videoExposureTotalCountList = null;
		try {
			videoExposureTotalCountList = videoExposureTotalCountDao.findByVideoId(ids);
		} catch (Exception e) {
			log.error("查询videoExposureTotalCount失败异常:[{}]", e.getMessage());
			addRedisVideoErrorInfo("totalCountError", updateNum, ids);
		}
		List<Long> addIds = Lists.newArrayList();
		for (Long videoId : ids) {
			boolean existId = false;
			if (CollectionUtils.isNotEmpty(videoExposureTotalCountList)) {
				for (VideoExposureTotalCount videoExposureTotalCount : videoExposureTotalCountList) {
					if (videoExposureTotalCount.getVideoId().equals(videoId)) {
						existId = true;
					}
				}
			}
			if (existId) {
				addIds.add(videoId);
			} else {
				try {
					VideoExposureTotalCount video= new VideoExposureTotalCount();
					video.setVideoId(videoId);
					video.setTotalCount(updateNum);
					videoExposureTotalCountDao.save(video);
				} catch (Exception e) {
					log.error("保存videoExposureTotalCount失败异常:[{}]", e.getMessage());
					List<Long> errorId = Lists.newArrayList();
					errorId.add(videoId);
					addRedisVideoErrorInfo("totalCountError", updateNum, errorId);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(addIds)) {
			try {
				videoExposureTotalCountDao.updateVideosTotalCntBatch(updateNum,addIds);
			} catch (Exception e) {
				log.error("更新videoExposureTotalCount失败异常:[{}]", e.getMessage());
				addRedisVideoErrorInfo("totalCountError", updateNum, addIds);
			}

		}
	}*/

	/**
	 * 更新视频曝光总数信息
	 * @param ids
	 */
	public void updateVideoExposureOneHourCountBatch(List<Long> ids, Long updateNum) {
		for (Long videoId : ids) {
			try {
				VideoExposureOneHourCount video= new VideoExposureOneHourCount();
				video.setVideoId(videoId);
				video.setCount(updateNum);
				video.setCreateTime(System.currentTimeMillis()/1000);
				videoExposureOneHourCountDao.save(video);
			} catch (Exception e) {
				log.error("保存videoExposureOneHourCount失败异常:[{}]", e.getMessage());
				List<Long> errorId = Lists.newArrayList();
				errorId.add(videoId);
				addRedisVideoErrorInfo("oneHourCountError", updateNum, errorId);
			}
		}
	}

	private void addRedisVideoErrorInfo(String key, Long mapKey, List<Long> mapValue) {
		log.info("更新曝光数失败：" + IPUtils.getHostAddress());
		String errorKey = RedisKeyConstant.VIDEO_EXPOSURE_DATA + key;
		for (int i=0;i<mapKey.intValue();i++) {
			for (Long videoId : mapValue) {
				redisDB3Service.lpush(errorKey, String.valueOf(videoId));
			}
		}
	}

	/**
	 * 更新曝光总数失败数据处理及删除1小时前的曝光数据：定时器每10分钟执行一次
	 */
	//@Scheduled(cron = "0 */10 * * * ?")
	@Transactional
	public void updateExposureNumErrorDatas() {
		RedisLock redisLock = new RedisLock(RedisKeyConstant.VIDEO_EXPOSURE_CLEAR_WRONG_DATAS_LOCK, RedisKeyConstant.VIDEO_EXPOSURE_CLEAR_WRONG_DATAS_SECONDS);
		if (redisLock.lock()) {
			/*String errorKey1 = RedisKeyConstant.VIDEO_EXPOSURE_DATA + "totalCountError";
			List<String> datas1 = redisDB3Service.lrange(errorKey1, 0, -1);
			redisDB3Service.del(errorKey1);
			if (CollectionUtils.isNotEmpty(datas1)) {
				updateVideoExposureInfo(datas1,0);
			}*/

			String errorKey2 = RedisKeyConstant.VIDEO_EXPOSURE_DATA + "oneHourCountError";
			List<String> datas2 = redisDB3Service.lrange(errorKey2, 0, -1);
			redisDB3Service.del(errorKey2);
			if (CollectionUtils.isNotEmpty(datas2)){
				updateVideoExposureInfo(datas2);
			}

			try {
				videoExposureOneHourCountDao.delVideosExposureCnt();
			} catch (Exception e) {
				log.error("删除videoExposureOneHourCount失败异常:[{}]", e.getMessage());
			}
			redisLock.unlock();
		} else {
			log.info("更新曝光数失败:未获取到redis锁。");
		}
	}
}