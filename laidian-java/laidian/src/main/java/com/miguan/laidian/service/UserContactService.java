package com.miguan.laidian.service;

import com.miguan.laidian.vo.UserContactVo;
import java.util.List;
import java.util.Map;

/**
 * Service
 * @author xy.chen
 * @date 2019-11-04
 **/

public interface UserContactService {

	/**
	 * 
	 * 通过条件查询通讯录列表
	 * 
	 **/
	List<UserContactVo>  findUserContactList(Map<String, Object> params);

	/**
	 * 
	 * 批量保存通讯录
	 * 
	 **/
	int batchSaveUserContact(List<UserContactVo> list);
}