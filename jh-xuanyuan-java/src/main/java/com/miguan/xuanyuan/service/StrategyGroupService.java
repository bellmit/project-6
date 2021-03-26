package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.dto.StrategyGroupDto;
import com.miguan.xuanyuan.dto.ab.AbFlowGroupParam;
import com.miguan.xuanyuan.dto.ab.AbItem;
import com.miguan.xuanyuan.dto.ab.AbLayer;
import com.miguan.xuanyuan.dto.request.AbStrategyCodeRequest;
import com.miguan.xuanyuan.dto.request.AbTestRequest;
import com.miguan.xuanyuan.dto.request.AbTestStatusRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import com.miguan.xuanyuan.vo.StrategyGroupVo;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StrategyGroupService {

    public List<StrategyGroupDto> getGroupList(long adPosId, int offset, int limit);

    public long insert(XyStrategyGroup xyStrategyGroup);

    public int update(XyStrategyGroup xyStrategyGroup);

    public XyStrategyGroup initStrategyGroupEntity(long positionId);

    public void initStrategyGroup(long positionId);

//    public List<AbLayer> getLayerInfo(String appPackage, Integer expId) throws ServiceException;


    public StrategyGroupVo getStrategyDetail(long strategyGroupId) throws ParseException, ServiceException;

    public XyStrategyGroup getDataById(long strategyGroupId);

    /**
     * 变更ab状态
     * @param abTestStatusRequest
     */
    public void changeAbStatus(AbTestStatusRequest abTestStatusRequest) throws ServiceException, ParseException;


    public void saveStragetyCode(StrategyGroupVo strategyGroupVo, AbStrategyCodeRequest abStrategyCodeRequest) throws ParseException, ServiceException;

    void addAbExpCheck(AbTestRequest abTestRequest) throws ServiceException;

    public void addAbExp(AbTestRequest abTestRequest, Long abId, String abExpCode, List<AbItem> abItemList) throws ServiceException;

    void editAbExpCheck(AbTestRequest abTestRequest) throws ServiceException;

    public void editAbExp(AbTestRequest abTestRequest, Long abId, List<AbItem> abItemList) throws ServiceException;

    Map<String, Object> getAbExpDetail(Long strategyGroupId) throws ServiceException;

    XyStrategyGroup getDataByPositionAndGroupName(Long strategyGroupId, Long positionId, String groupName);

    Map<String, Object> getGroupDetail(Long position) throws ServiceException;

    int updateStrategyGroupByAbId(Long abId, Integer status);

    void deleteByAppId(Long appId);

    void deleteByPositionId(Long positionId);
}
