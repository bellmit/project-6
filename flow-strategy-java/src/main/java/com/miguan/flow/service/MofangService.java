package com.miguan.flow.service;

import java.util.Map;

/**
 * @Description 魔方service
 **/
public interface MofangService {
    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    boolean stoppedByMofang(Map<String, Object> param);
}
