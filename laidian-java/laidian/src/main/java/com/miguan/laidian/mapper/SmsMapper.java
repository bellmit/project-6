package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.SmsVo;

import java.util.List;
import java.util.Map;

/**
 * 短信记录Mapper
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */

public interface SmsMapper {

	/**
	 * 查询最新一条短信记录
	 * @param data
	 * @return
	 */
	SmsVo findTimeMsg(Map<String, Object> data);

    /**
     * 查询某号码某种类型当天已发送次数
     * @param data
     * @return
     */
    List<SmsVo> countDayTime(Map<String, Object> data);
    

    /**
     * 根据订单号修改短信记录
     * @param paramMap
     * @return
     */
    int updateByOrderNo(Map<String, Object> paramMap);

    int save(SmsVo smsVo);

	int updateSelective(Map<String, Object> paramMap);

}
