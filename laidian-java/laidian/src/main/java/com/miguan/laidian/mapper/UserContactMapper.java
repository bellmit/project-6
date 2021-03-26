package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.UserContactVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Mapper
 * @author xy.chen
 * @date 2019-11-04
 **/

public interface UserContactMapper {

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
	int batchSaveUserContact(@Param("userContacts") List<UserContactVo> list);
}