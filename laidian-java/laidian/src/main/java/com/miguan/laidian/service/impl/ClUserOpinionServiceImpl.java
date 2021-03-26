package com.miguan.laidian.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.mapper.ClUserOpinionMapper;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.ClUserOpinionService;
import com.miguan.laidian.vo.ClUserOpinionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 用户意见反馈表ServiceImpl
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("clUserOpinionService")
public class ClUserOpinionServiceImpl implements ClUserOpinionService {

	@Resource
	private ClUserOpinionMapper clUserOpinionMapper;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Override
	public ClUserOpinionVo getClUserOpinionById(String id) {
		ClUserOpinionVo clUserOpinionById = clUserOpinionMapper.getClUserOpinionById(id);
		clUserOpinionMapper.updateUserOpinionState(id);
		return clUserOpinionById;
	}

	@Override
	public Page<ClUserOpinionVo> findClUserOpinionList(ClUserOpinionVo clUserOpinionVo, int currentPage, int pageSize){
		Map<String, Object> params = new HashedMap();
		params.put("deviceId",clUserOpinionVo.getDeviceId());
		params.put("state",ClUserOpinionVo.PROCESSED);
		PageHelper.startPage(currentPage,pageSize);
		List<ClUserOpinionVo> clUserOpinionList = clUserOpinionMapper.findClUserOpinionList(params);
		return (Page<ClUserOpinionVo>) clUserOpinionList;
	}

	@Override
	public int saveClUserOpinion(ClUserOpinionVo clUserOpinionVo){
		Integer num = clUserOpinionMapper.saveClUserOpinion(clUserOpinionVo);
		sendTextToMQ(clUserOpinionVo);
		return num;
	}

	/**
	 * 向钉钉推送用户意见反馈内容
	 * @param opinionVo
	 */
	public void sendTextToMQ(ClUserOpinionVo opinionVo){
		if (opinionVo != null && StringUtils.isNotEmpty(opinionVo.getContent())) {
			String dataStr = JSON.toJSONString(opinionVo);
			rabbitTemplate.convertAndSend(RabbitMQConstant.DINGTALK_ROBOT_MSG_EXCHANGE, RabbitMQConstant.DINGTALK_ROBOT_MSG_KEY, dataStr);
		}
	}
}