package com.miguan.laidian.mapper;


import com.miguan.laidian.vo.SmsTplVo;

import java.util.Map;

/**
 * 短信记录Mapper
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-09
 */
public interface SmsTplMapper {

    /**
     * 查询当前启用的短信设置的短信模板
     * @param params
     * @return
     */
    SmsTplVo querySmsTplInfo(Map<String, Object> params);

    SmsTplVo findSelective(Map<String, Object> params);

}
