package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.AudioDownloadVo;
import java.util.List;
import java.util.Map;

/**
 * 音频下载记录表Mapper
 * @author xy.chen
 * @date 2020-05-22
 **/

public interface AudioDownloadMapper{

	/**
	 * 
	 * 通过条件查询音频下载记录列表
	 * 
	 **/
	int  delAudioDownload();

	/**
	 * 
	 * 新增音频下载记录信息
	 * 
	 **/
	int saveAudioDownload(AudioDownloadVo audioDownloadVo);
}