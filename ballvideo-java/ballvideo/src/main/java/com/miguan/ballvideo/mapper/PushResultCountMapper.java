package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.push.PushResultCountVo;

public interface PushResultCountMapper {


	/**
	 * 查询推送结果统计信息
	 * @param countVo
	 * @return
	 */
	PushResultCountVo getPushResultCountInfo(PushResultCountVo countVo);

	/**
	 * 
	 * 新增推送结果统计信息
	 * @param countVo
	 * @return
	 * 
	 **/
	int savePushResultCountInfo(PushResultCountVo countVo);

	/**
	 * 
	 * 修改推送结果统计信息
	 * @param countVo
	 * @return
	 * 
	 **/
	int updatePushResultCountInfo(PushResultCountVo countVo);

}