package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.constants.PushArticleConstant;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.mapper.ClUserOpinionMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.ClUserOpinionService;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import com.miguan.ballvideo.vo.ClUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
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
	private ClUserMapper clUserMapper;
	@Resource
	private RabbitTemplate rabbitTemplate;

	@Override
	@Transactional
	public ClUserOpinionVo getClUserOpinionById(String id) {
		ClUserOpinionVo clUserOpinionById = clUserOpinionMapper.getClUserOpinionById(id);
		clUserOpinionMapper.updateUserOpinionState(id);
		return clUserOpinionById;
	}

	@Override
	public int updateUserOpinionStateByUserId(String userId) {
		return clUserOpinionMapper.updateUserOpinionStateByUserId(userId);
	}


	@Override
	public List<ClUserOpinionVo>  findClUserOpinionList(Map<String, Object> params){
		return clUserOpinionMapper.findClUserOpinionList(params);
	}

	@Override
	public Page<ClUserOpinionVo> findClUserOpinionList(ClUserOpinionVo clUserOpinionVo, int currentPage, int pageSize) {
		Map<String, Object> params = new HashedMap();
		params.put("userId",clUserOpinionVo.getUserId());
		params.put("state",ClUserOpinionVo.PROCESSED);
		params.put("appPackage",clUserOpinionVo.getAppPackage());
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

	@Override
	public int saveClUserOpinionPlus(ClUserOpinionVo clUserOpinionVo){
		return clUserOpinionMapper.saveClUserOpinionPlus(clUserOpinionVo);
	}

	@Override
	public void saveClUserOpinionByPushArticle(PushArticle pushArticle) {
		/* user_id 是用户id清单，用,隔开；要循环插入 */
		if(PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
			String userIds = pushArticle.getUserIds();
			String[] userIdArray = userIds.split(",");
			for (String str : userIdArray) {
				try {
					// 具体内容走消息队列
					ClUserOpinionVo clUserOpinionVo = new ClUserOpinionVo();
					clUserOpinionVo.setState(ClUserOpinionVo.PROCESSED);
					clUserOpinionVo.setTitle(pushArticle.getTitle());
					clUserOpinionVo.setType(pushArticle.getType());
					clUserOpinionVo.setTypeValue(pushArticle.getTypeValue());
					clUserOpinionVo.setUserId(Long.valueOf(str));
					clUserOpinionVo.setContent(pushArticle.getNoteContent());
					String dataStr = JSON.toJSONString(clUserOpinionVo);
					rabbitTemplate.convertAndSend(RabbitMQConstant.UserOption_SAVE_EXCHANGE, RabbitMQConstant.UserOption_SAVE_KEY, dataStr);
					//log.info("记录系统消息 发送到指定用户 用户id:" + str + ",保存消息" +pushArticle.getTitle() +"发送成功:[{}]");
				} catch (Exception e) {
					log.error("记录系统消息 发送到指定用户 用户id:" + str + ",保存消息" +pushArticle.getTitle() +"异常:[{}]", e.getMessage());
				}
			}
		}else{
			/* 全推的话，根据包名取用户，批量插入 */
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appPackage", pushArticle.getAppPackage());
			// params.put("virtual_user", 0);
			List<ClUserVo> clUserVoList = clUserMapper.findClUserList(params);
			clUserVoList.forEach(clUserVo->{
				try {
					// 具体内容走消息队列
					ClUserOpinionVo clUserOpinionVo = new ClUserOpinionVo();
					clUserOpinionVo.setState(ClUserOpinionVo.PROCESSED);
					clUserOpinionVo.setTitle(pushArticle.getTitle());
					clUserOpinionVo.setType(pushArticle.getType());
					clUserOpinionVo.setTypeValue(pushArticle.getTypeValue());
					clUserOpinionVo.setUserId(clUserVo.getId());
					clUserOpinionVo.setContent(pushArticle.getNoteContent());
					String dataStr = JSON.toJSONString(clUserOpinionVo);
					rabbitTemplate.convertAndSend(RabbitMQConstant.UserOption_SAVE_EXCHANGE, RabbitMQConstant.UserOption_SAVE_KEY, dataStr);
				} catch (Exception e) {
					log.error("记录系统消息 发送到所有用户 用户id:" + clUserVo.getId() + ",保存消息" +pushArticle.getTitle() +"异常:[{}]", e.getMessage());
				}
			});

		}
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