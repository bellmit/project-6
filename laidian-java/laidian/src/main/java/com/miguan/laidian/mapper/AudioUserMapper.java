package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.AudioUserVo;
import java.util.List;
import java.util.Map;

/**
 * 音频用户关系表Mapper
 * @author xy.chen
 * @date 2020-05-22
 **/

public interface AudioUserMapper{


	/**
	 * 
	 * 通过音频ID和设备ID查询音频用户关系信息
	 * 
	 **/
	AudioUserVo  getAudioUserByAudioIdAndDeviceId(Map<String, Object> params);
	/**
	 * 
	 * 新增音频用户关系信息
	 * 
	 **/
	int saveAudioUser(AudioUserVo audioUserVo);

	/**
	 * 修改音频用户关系表信息
	 *
	 */
	int updateAudioCount(Map<String, Object> params);

}