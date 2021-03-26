package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.AudioCatVo;
import com.miguan.laidian.vo.AudioDownloadVo;
import com.miguan.laidian.vo.AudioVo;
import java.util.List;
import java.util.Map;

/**
 * 音频信息表Mapper
 * @author xy.chen
 * @date 2020-05-22
 **/

public interface AudioMapper{

	/**
	 *
	 * 查询音频类别列表
	 *
	 **/
	List<AudioCatVo>  findAudioCatList();

	/**
	 * 
	 * 通过条件查询音频信息列表
	 * 
	 **/
	List<AudioVo>  findAudioList(Map<String, Object> params);

	/**
	 * 
	 * 更新收藏数、分享数、下载数、试听数
	 * 
	 **/
	int updateAudioCount(Map<String, Object> params);
}