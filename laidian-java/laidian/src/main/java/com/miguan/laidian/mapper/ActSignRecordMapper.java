package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.ActSignRecordVo;

import java.util.Map;

/**
 * 签到记录Mapper
 * @author xy.chen
 * @date 2020-08-14
 **/

public interface ActSignRecordMapper {

	/**
	 * 保存签到记录
	 *
	 * @return
	 */
	int saveActSignRecord(ActSignRecordVo ctSignRecordVo);

	/**
	 *
	 * 根据条件查询签到记录
	 *
	 **/
	ActSignRecordVo queryActSignRecord(Map<String, Object> params);
}