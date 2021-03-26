package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.SmsConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 短信配置Mapper
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */

public interface SmsConfigMapper {

    /**
     * 查询启用状态的短信配置
     * @param params
     * @return
     */
    List<SmsConfigVo> queryEnableSmsConfig(Map<String, Object> params);
  
}
