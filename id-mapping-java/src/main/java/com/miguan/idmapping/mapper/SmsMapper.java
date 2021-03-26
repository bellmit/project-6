package com.miguan.idmapping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.idmapping.vo.SmsVo;

import java.util.Map;

/**
 * 短信记录Mapper
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */

public interface SmsMapper extends BaseMapper<SmsVo> {

	/**
	 * 查询最新一条短信记录
	 * @param data
	 * @return
	 */
	SmsVo findTimeMsg(Map<String, Object> data);

	int updateSelective(Map<String, Object> paramMap);

}
