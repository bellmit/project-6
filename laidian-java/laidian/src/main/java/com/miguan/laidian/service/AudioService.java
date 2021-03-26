package com.miguan.laidian.service;

import com.github.pagehelper.Page;
import com.miguan.laidian.vo.AudioCatVo;
import com.miguan.laidian.vo.AudioVo;
import java.util.List;
import java.util.Map;

/**
 * 音频Service
 * @author xy.chen
 * @date 2020-05-22
 **/

public interface AudioService {

	/**
	 *
	 * 查询音频类别列表
	 *
	 **/
	List<AudioCatVo>  findAudioCatList();

	/**
	 * 根据音频ID查询音频详情
	 *
	 * @param audioId
	 * @return
	 */
	AudioVo findAudioDetail(Long audioId);

	/**
	 * 
	 * 通过条件查询音频信息列表
	 * 
	 **/
	Page<AudioVo> findAudioList(Map<String, Object> params, int currentPage, int pageSize);

	/**
	 * 消息队列
	 *
	 * @param params
	 */
	void updateCountSendMQ(Map<String, Object> params);

	/**
	 * 更新收藏数、分享数、下载数、试听数
	 *
	 * @param params
	 */
	void updateAudioCount(Map<String, Object> params);
}