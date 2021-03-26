package com.miguan.report.mapper;

import com.miguan.report.vo.PerShowVo;

import java.util.List;
import java.util.Map;

/**人均展示统计 MAPPER
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public interface PerShowMapper {

    /**
     * 根据平台或应用分组统计人均展现
     * @param params
     * @return
     */
    List<PerShowVo> queryStaGroupByAppOrPlat(Map<String, Object> params);
}
