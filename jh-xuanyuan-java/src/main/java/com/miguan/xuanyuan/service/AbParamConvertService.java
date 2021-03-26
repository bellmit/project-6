package com.miguan.xuanyuan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.StrategyGroupConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.dto.ab.AbFlowGroupParam;
import com.miguan.xuanyuan.dto.ab.AbItem;
import com.miguan.xuanyuan.dto.request.AbTestRequest;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AbParamConvertService {

    @Resource
    XyAdPositionService xyAdPositionService;

    @Resource
    StrategyGroupService strategyGroupService;


    /**
     * 参数格式转换
     *
     * @param abTestRequest
     * @return
     * @throws ServiceException
     */
    public AbFlowGroupParam convertToAbFlowGroupParam(AbTestRequest abTestRequest) throws ServiceException {

        Long positionId = abTestRequest.getPositionId();
        Long strategyGroupId = abTestRequest.getStrategyGroupId();

        AdPositionDetailDto positionDetail = xyAdPositionService.getPositionDetail(positionId);
        if (positionDetail == null) {
            throw new ServiceException("广告位不存在");
        }

        String abExpCode = ""; //实验标识
        Integer abId = null; //ab实验id
        String layerName = ""; //分层名称
        Integer status = 0;

        XyStrategyGroup strategyGroupInfo = new XyStrategyGroup();
        if (strategyGroupId != null && strategyGroupId > 0) {
            strategyGroupInfo = strategyGroupService.getDataById(strategyGroupId);
            if (strategyGroupInfo == null) {
                throw new ServiceException("分组不存在");
            }
            abExpCode = strategyGroupInfo.getAbExpCode();
            abId = strategyGroupInfo.getAbId().intValue();
            status = strategyGroupInfo.getStatus();
        }

        String layerId = (StringUtils.isEmpty(abTestRequest.getLayerId()) || abTestRequest.getLayerId().equals("0")) ? "-1" : abTestRequest.getLayerId();

        AbFlowGroupParam abFlowGroupParam = new AbFlowGroupParam();
        abFlowGroupParam.setApp_id(positionDetail.getAppId().intValue());
        abFlowGroupParam.setApp_key(positionDetail.getAppkey());
        abFlowGroupParam.setApp_name(positionDetail.getAppName());
        abFlowGroupParam.setCode(abExpCode);
        abFlowGroupParam.setApp_type(positionDetail.getPackageName());
        abFlowGroupParam.setDoc_url("");
        abFlowGroupParam.setName(abTestRequest.getExpName());
        abFlowGroupParam.setPosition_id(positionId.intValue());
        abFlowGroupParam.setPosition_name(positionDetail.getPositionName());
        abFlowGroupParam.setLayer_id(Integer.parseInt(layerId));
        abFlowGroupParam.setExperiment_id(abId);
        abFlowGroupParam.setLayer_name(layerName);
        abFlowGroupParam.setChannelOperation(abTestRequest.getChannelOperation());
        abFlowGroupParam.setChannel_type(abTestRequest.getChannelType());
        abFlowGroupParam.setChannel_ids(abTestRequest.getChannelIds());
        abFlowGroupParam.setVersion_type(abTestRequest.getVersionType());
        abFlowGroupParam.setVersionOperation(abTestRequest.getVersionOperation());
        abFlowGroupParam.setVersion_ids(abTestRequest.getVersionIds());
        abFlowGroupParam.setIs_new(abTestRequest.getIsNew());
        abFlowGroupParam.setState(status);
        abFlowGroupParam.setUse_default(true);
        abFlowGroupParam.setPub_time("");
        abFlowGroupParam.setState_bak(0);

        List<AbItem> flowRate = abTestRequest.getFlowRate();
        List<HashMap<String, Object>> ratioList  = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowRate)) {
            for (AbItem abItem : flowRate) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("traffic", abItem.getVal() * 10);
                String id = "-1";
                if (abItem.getType().equals(StrategyGroupConstant.STRATEGY_TYPE_TOTAL)) {
                    item.put("name", "总流量");
                } else if (abItem.getType().equals(StrategyGroupConstant.STRATEGY_TYPE_CONTRAST)) {
                    item.put("name", "对照组");
                } else {
                    item.put("name", "测试组");
                }
                if (abItem.getAbItemId() != null && abItem.getAbItemId() > 0L) {
                    id = String.valueOf(abItem.getAbItemId());
                }
                item.put("id", id);
                ratioList.add(item);
            }
        }
        String radioJson = JSON.toJSONString(ratioList);
        abFlowGroupParam.setRatioJson(radioJson);

        return abFlowGroupParam;
    }
}
