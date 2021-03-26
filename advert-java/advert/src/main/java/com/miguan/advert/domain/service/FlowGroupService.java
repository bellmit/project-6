package com.miguan.advert.domain.service;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.vo.result.ABFlowGroupVo;
import com.miguan.advert.domain.vo.result.AdvCodeInfoVo;
import com.miguan.advert.domain.vo.result.TestInVo;
import java.util.List;

public interface FlowGroupService {
    /**
     * 获取流量分组列表
     * @param appPackage
     * @return
     */
    ResultMap<List<ABFlowGroupVo>> getLst(String appPackage);

    /**
     * 修改流量分组名称
     * @param flowId
     * @param abFlowId
     * @param name
     * @param ip
     */
    ResultMap<ABFlowGroupVo> updateFlowName(String flowId, String abFlowId, String name, String ip);

    /**
     * 新增流量分组名称
     * @param name
     * @param abFlowId
     * @param ip
     */
    ResultMap<ABFlowGroupVo> insertFlowGroup(String positionId, String name, String abFlowId, String type, String ip);

    /**
     * 删除流量分组
     * @param flowId
     * @param ip
     */
    void deleteFlowGroup(String flowId, String ip);


    ResultMap<ABFlowGroupVo> addEditTestGroup(String flowId, List<String> testIds, String testState, String ip);

    void saveFlowTestGroup(List<TestInVo> testArr, String ip);

    /**
     * 获取广告位排序
     * @param positionId
     * @param channelId
     * @param userType
     * @param city
     * @return
     */
    public abstract List<AdvCodeInfoVo> getAdOrder(String positionId, String packageName, String channelId, int userType, String city);
}
