package com.miguan.bigdata.mapper;


import com.miguan.bigdata.vo.MonitorVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 监测接口mapper
 * @Author zhangbinglin
 * @Date 2020/11/6 9:01
 **/
public interface MonitorMapper {

    /**
     *
     * @param params
     * @return
     */
    List<MonitorVo> monitorAdAction(Map<String, Object> params);

    List<MonitorVo> monitorUserAction(Map<String, Object> params);

    List<MonitorVo> monitorLdUserAction(Map<String, Object> params);


}
