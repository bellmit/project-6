package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.ClUserOpinionVo;
import java.util.List;
import java.util.Map;

/**
 * 用户意见反馈表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserOpinionMapper{


	/**
	 * 
	 * 通过ID查询用户意见反馈信息
	 * 
	 **/
	ClUserOpinionVo  getClUserOpinionById(String id);

	/**
	 * 
	 * 通过条件查询用户意见反馈列表
	 * 
	 **/
	List<ClUserOpinionVo>  findClUserOpinionList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户意见反馈信息
	 * 
	 **/
	int saveClUserOpinion(ClUserOpinionVo clUserOpinionVo);

	/**
	 *
	 * 新增系统消息（审核通过 的时候调用）
	 *
	 **/
	int saveClUserOpinionPlus(ClUserOpinionVo clUserOpinionVo);

	/**
	 *
	 * 用户已读（修改用户已读状态）
	 *
	 **/
	int updateUserOpinionState(String id);

	/**
	 *
	 * 用户已读（修改用户下所有系统消息为已读状态）
	 *
	 **/
	int updateUserOpinionStateByUserId(String UserId);

	/**
	 *
	 * 查看用户已被回复的数量
	 * 2019年9月26日10:03:16
	 *	HYL
	 **/
	int findClUserOpinionNumber(Map<String, Object> map);
}