package com.miguan.laidian.service.impl;

import com.miguan.laidian.mapper.UserContactMapper;
import com.miguan.laidian.service.UserContactService;
import com.miguan.laidian.vo.UserContactVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ServiceImpl
 * @author xy.chen
 * @date 2019-11-04
 **/

@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {

	@Resource
	private UserContactMapper userContactMapper;

	@Override
	public List<UserContactVo>  findUserContactList(Map<String, Object> params){
		return userContactMapper.findUserContactList(params);
	}

	@Override
	public int batchSaveUserContact(List<UserContactVo> list){
		return userContactMapper.batchSaveUserContact(list);
	}
}