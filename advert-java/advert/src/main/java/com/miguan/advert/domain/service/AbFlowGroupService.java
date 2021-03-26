package com.miguan.advert.domain.service;

import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.vo.interactive.AbLayer;
import com.miguan.advert.domain.vo.interactive.AppInfo;
import com.miguan.advert.domain.vo.request.AbFlowGroupParam;
import com.miguan.advert.domain.vo.result.ABFlowGroupVo;

import java.util.List;
import java.util.Map;

public interface AbFlowGroupService {
    ResultMap<ABFlowGroupVo> saveFlowInfo(AbFlowGroupParam abFlowGroupParam, String flowId, String ip) throws ServiceException;

    List<AbLayer> getLayerInfo(String app_id, Integer exp_id) throws ServiceException;

    ResultMap getVersionInfo(String appType);

    List<AppInfo> appList() throws ServiceException;

    AbFlowGroupParam findAbflow(Integer positionId, String flowId, String appType, String ip) throws ServiceException;

    void deleteAbflow(String flowId, String appType, String ip) throws ServiceException;


    void sendEditState(Map<String, Object> param) throws ServiceException;

    Integer searchAppId(String app_type) throws ServiceException;

    void sendEditState(String app_type, Integer state, String ab_flow_id, String ip) throws ServiceException;
}
