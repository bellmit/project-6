package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.ClUserVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserMapper{

	/**
	 * 
	 * 通过条件查询用户列表
	 * 
	 **/
	List<ClUserVo> findClUserList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户信息
	 * 
	 **/
	int saveClUser(ClUserVo clUserVo);

	/**
	 * 
	 * 修改用户信息
	 * 
	 **/
	int updateClUser(ClUserVo clUserVo);

	/**
	 * 查询所有用户的token信息
	 * @return
	 */
	List<ClUserVo> findAllTokens();

}