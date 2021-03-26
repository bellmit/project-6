package com.miguan.report.mapper;

import com.miguan.report.vo.ActiveValueVo;

import java.util.List;
import java.util.Map;

/**日活均价值统计 MAPPER
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public interface ActiveValueMapper {

    /**
     * 根据平台或应用分组统计
     * @param params
     * @return
     */
    List<ActiveValueVo> queryStaGroupByAppOrPlat(Map<String, Object> params);
}
