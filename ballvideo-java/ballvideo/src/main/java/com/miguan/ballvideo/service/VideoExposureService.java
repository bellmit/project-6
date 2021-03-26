package com.miguan.ballvideo.service;

import java.util.List;

/**
 * 小视频列表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface VideoExposureService {

	boolean videoExposureCountSendToMQ(String deviceId, String videoId);

	void videoExposureSendToRedis(String jsonMsg);

	void videoExposureCountInfo(List<String> list);
}