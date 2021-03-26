package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.advert.common.constants.FlowGroupConstant;
import com.miguan.advert.common.util.PageInfoUtil;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.domain.mapper.FlowTestMapper;
import com.miguan.advert.domain.mapper.PositionInfoMapper;
import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.service.PositionInfoService;
import com.miguan.advert.domain.service.ToolMofangService;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import com.miguan.advert.domain.vo.request.ConfigAddVo;
import com.miguan.advert.domain.vo.result.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PositionInfoServiceImpl implements PositionInfoService {

    @Resource
    private PositionInfoMapper infoMapper;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private FlowTestMapper flowTestMapper;

    @Override
    public PositionListVo getData(HttpServletRequest request, Integer type, String s_data, Integer page, Integer page_size) {
        PositionListVo positionListVo = new PositionListVo();
        Map<String, Object> param = new HashMap<>();
        if (type != null && type == 1) {
            param.put("positionIds", s_data);
        } else if (type != null && type == 2) {
            s_data = "'" + s_data.replace(",","','") + "'";
            param.put("codeIds", s_data);
        }
        List<PositionDetailInfoVo> positionDetailInfoVoList = infoMapper.getConfigInfoList(param);
        if (CollectionUtils.isNotEmpty(positionDetailInfoVoList)) {
            String url = request.getRequestURL().toString();
            PageResultVo pageResultVo = PageInfoUtil.getPageInfo(url, positionDetailInfoVoList.size(), page, page_size);
            BeanUtils.copyProperties(pageResultVo, positionListVo);
            List<PositionDetailInfoVo> positionDetailInfoVos = positionDetailInfoVoList.subList((pageResultVo.getFrom() - 1), pageResultVo.getTo());
            String ids = positionDetailInfoVos.stream().map(p -> p.getId() + "" ).collect(Collectors.joining(","));
            String positionIds = positionDetailInfoVos.stream().map(p -> p.getPosition_id() + "" ).collect(Collectors.joining(","));
            param.put("ids", ids);
            List<TypeInfoVo> typeInfoVoList = infoMapper.getTypeData(param);
            //这边排序吧

            List<FlowCountVo> flowCountVos = flowTestMapper.getAllAdvFlowConfLst(positionIds);
            Map<Integer, Integer> flowConfigMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(flowCountVos)) {
                flowConfigMap = flowCountVos.stream().collect(Collectors.toMap(FlowCountVo::getPosition_id, FlowCountVo::getNum));
            }
            for (PositionDetailInfoVo infoVo : positionDetailInfoVos) {
                boolean exFlag = false;
                List<TypeInfoVo> typeInfoVos = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(typeInfoVoList)) {
                    for (TypeInfoVo typeInfoVo : typeInfoVoList) {
                        if (infoVo.getId().equals(typeInfoVo.getConfig_id())) {
                            if (infoVo.getComputer() > 1) {
                                typeInfoVo.setOption_value(typeInfoVos.size() + 1);
                            }
                            if(typeInfoVo.getChannel_type() == 1){
                                typeInfoVo.setChannel_name("");
                            } else {
                                String channelNames = getChannelNames(typeInfoVo.getChannel_ids());
                                typeInfoVo.setChannel_name(channelNames);
                            }
                            typeInfoVos.add(typeInfoVo);
                            exFlag = true;
                        }
                    }
                }
                //手动配比这边排序
                if(CollectionUtils.isNotEmpty(typeInfoVos)){
                    if(typeInfoVos.get(0).getComputer() == FlowGroupConstant.HANDLER_MATCHING){
                        typeInfoVos.sort(Comparator.comparing(TypeInfoVo::getNumber));
                    }
                }
                if (!exFlag) {
                    TypeInfoVo typeInfoVo = new TypeInfoVo();
                    typeInfoVos.add(typeInfoVo);
                    infoVo.setIs_site(0);
                } else {
                    infoVo.setIs_site(1);
                }
                infoVo.setType_data(typeInfoVos);
                if (flowConfigMap != null) {
                    Integer cnt = flowConfigMap.get(infoVo.getPosition_id());
                    if (cnt != null) {
                        if (cnt == 1) {
                            infoVo.setGroup_number(1);
                        } else if (cnt > 1) {
                            infoVo.setGroup_number(2);
                        }
                    }
                }
            }
            positionListVo.setData(positionDetailInfoVos);
        } else {
            List<PositionDetailInfoVo> positionDetailInfoVos = Lists.newArrayList();
            positionListVo.setData(positionDetailInfoVos);
        }
        return positionListVo;
    }

    @Override
    public List<AppAdPositionVo> getAppAdPositionName(String app_package, Integer mobile_type) {
        Map<String, Object> param = new HashMap<>();
        param.put("appPackage", app_package);
        param.put("mobileType", mobile_type);
        return infoMapper.getAppAdPositionName(param);
    }

    @Override
    public List<PositionInfoVo> getPosition(Integer position_id) {
        Map<String, Object> param = new HashMap<>();
        param.put("positionId", position_id);
        List<PositionInfoVo> positionInfoVos = infoMapper.getPosition(param);
        if (CollectionUtils.isNotEmpty(positionInfoVos)) {
            List<ConfigInfoVo> codeConfigs = infoMapper.getCodeConfig(param);
            List<ChannelInfoVo> channelInfoVos = toolMofangService.findChannelInfo();
            for (PositionInfoVo infoVo : positionInfoVos) {
                getChannelName(channelInfoVos, infoVo);
                getIsHave(codeConfigs, infoVo);
            }
        }
        return positionInfoVos;
    }

    /**
     * 获取渠道名称
     * @param channelIds
     * @return
     */
    private String getChannelNames(String channelIds) {
        String channelNames = "";
        if(StringUtil.isEmpty(channelIds)){
            channelNames = "全部";
        }
        List<ChannelInfoVo> channelInfoVoList = toolMofangService.findChannelInfoByKeys(channelIds);
        if (CollectionUtils.isNotEmpty(channelInfoVoList)) {
            channelNames = channelInfoVoList.stream().map(channelInfoVo ->
                    channelInfoVo.getChannelName()).collect(Collectors.joining(","));
        }
        return channelNames;
    }

    //查询是否配置
    private void getIsHave(List<ConfigInfoVo> codeConfigs, PositionInfoVo infoVo) {
        if (CollectionUtils.isNotEmpty(codeConfigs)) {
            boolean exitFlag = false;
            for (ConfigInfoVo codeConfigVo : codeConfigs) {
                if (infoVo.getId().equals(codeConfigVo.getId())) {
                    infoVo.setOption_value(codeConfigVo.getOption_value());
                    exitFlag = true;
                    break;
                }
            }
            if (exitFlag) {
                infoVo.setIs_have(1);
            } else {
                infoVo.setIs_have(0);
            }
        }
    }

    //查询是否配置
    private void getIsHave2(List<ConfigInfoVo> codeConfigs, PositionInfoVo infoVo, List<PositionInfoVo> positionResultList) {
        if (CollectionUtils.isNotEmpty(codeConfigs)) {
            boolean exitFlag = false;
            for (ConfigInfoVo configInfoVo : codeConfigs) {
                if (infoVo.getId().equals(configInfoVo.getId())) {
                    infoVo.setOption_value(configInfoVo.getOption_value());
                    exitFlag = true;
                    break;
                }
            }
            if (exitFlag) {
                infoVo.setIs_have(1);
                positionResultList.add(infoVo);
            } else {
                infoVo.setIs_have(0);
            }
        }
    }

    //获取渠道名称信息
    private void getChannelName(List<ChannelInfoVo> channelInfoVos, PositionInfoVo infoVo) {
        String channelIds = infoVo.getChannel_ids();
        if (StringUtils.isNotEmpty(channelIds)) {
            String[] channelIdStr = channelIds.split(",");
            String channelNames = "";
            for (int i=0;i<channelIdStr.length;i++) {
                for (ChannelInfoVo channelInfoVo : channelInfoVos) {
                    if (channelIdStr[i].equals(channelInfoVo.getChannelId())) {
                        if (i == channelIdStr.length - 1) {
                            channelNames += channelInfoVo.getChannelName();
                        } else {
                            channelNames += channelInfoVo.getChannelName() + "、";
                        }
                        break;
                    }
                }
            }
            infoVo.setChannel_name(channelNames);
        }
    }


    @Override
    public List<CodeListVo> getCode(Integer position_id) {
        List<CodeListVo> resultList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("positionId", position_id);
        List<PositionInfoVo> positionInfoVos = infoMapper.getPosition(param);
        if (CollectionUtils.isNotEmpty(positionInfoVos)) {
            List<PositionInfoVo> positionResultList = Lists.newArrayList();
            List<ConfigInfoVo> codeConfigs = infoMapper.getCodeConfig(param);
            List<ChannelInfoVo> channelInfoVos = toolMofangService.findChannelInfo();
            for (PositionInfoVo infoVo : positionInfoVos) {
                getChannelName(channelInfoVos, infoVo);
                getIsHave2(codeConfigs, infoVo, positionResultList);
            }
            CodeListVo codeListVo = new CodeListVo();
            codeListVo.setCode_list(positionResultList);
            if (CollectionUtils.isNotEmpty(codeConfigs)) {
                Integer configId = codeConfigs.get(0).getConfig_id();
                codeListVo.setConfig_id(configId);
            }
            resultList.add(codeListVo);
        }

        return resultList;
    }

    @Override
    @Transactional
    public ResultMap saveConfigInfo(String insertStr, String deleteStr, ConfigAddVo rateData) {
        Integer computer = rateData.getComputer();
        Integer configId = rateData.getConfig_id();
        List<PositionInfoVo> dataList = rateData.getData();
        List<ConfigInfoVo> addList = new ArrayList<>();
        List<ConfigInfoVo> updateList = new ArrayList<>();
        String addStr = insertStr;
        if (StringUtils.isNotEmpty(insertStr)) {
            addStr = insertStr + ",";
        }
        String msg = arrangeDataList(computer, configId, dataList, addList, updateList, addStr);
        if (msg != "") {
            return ResultMap.error(202,msg);
        }
        saveData(deleteStr, computer, configId, addList, updateList);
        return ResultMap.success("","提交成功");
    }

    /**
     * 保存数据
     * @param deleteStr
     * @param computer
     * @param configId
     * @param addList
     * @param updateList
     */
    private void saveData(String deleteStr, Integer computer, Integer configId, List<ConfigInfoVo> addList, List<ConfigInfoVo> updateList) {
        Map<String, Object> paramUpdate = new HashMap<>(5);
        paramUpdate.put("computer", computer);
        paramUpdate.put("configId", configId);
        infoMapper.updateConfig(paramUpdate);
        delConfigData(deleteStr, configId);
        if (CollectionUtils.isNotEmpty(addList)) {
            infoMapper.addCodeConfig(addList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            for (ConfigInfoVo configInfoVo : updateList) {
                paramUpdate.put("optionValue", configInfoVo.getOption_value());
                paramUpdate.put("codeId", configInfoVo.getId());
                infoMapper.updateCodeConfig(paramUpdate);
            }
        }
    }

    /**
     * 删除配置信息
     * @param deleteStr
     * @param configId
     */
    private void delConfigData(String deleteStr, Integer configId) {
        if (StringUtils.isNotEmpty(deleteStr)) {
            Map<String, Object> paramDel = new HashMap<>(2);
            paramDel.put("configId", configId);
            paramDel.put("codeIds", deleteStr);
            infoMapper.deleteCodeConfig(paramDel);
        }
    }

    /**
     * 配置信息处理
     * @param computer
     * @param dataList
     * @param addList
     * @param updateList
     * @param addStr
     * @return
     */
    private String arrangeDataList(Integer computer, Integer configId, List<PositionInfoVo> dataList, List<ConfigInfoVo> addList, List<ConfigInfoVo> updateList, String addStr) {
        String msg = "";
        int sortNum = 1;
        for (int i=0; i<dataList.size(); i++) {
            //数据类型：1-新增，2-修改
            int dataType;
            PositionInfoVo positionInfoVo = dataList.get(i);
            String codeId = positionInfoVo.getId() + ",";
            if (addStr.contains(codeId)) {
                dataType = 1;
            } else {
                dataType = 2;
            }
            Integer optionValue = positionInfoVo.getOption_value();
            if (computer == 1) {
                if (optionValue == null || optionValue == 0) {
                    msg = "概率不能为0或者必填";
                    break;
                }
            } else {
                dataList.get(i).setOption_value(sortNum);
                sortNum += 1;
            }
            ConfigInfoVo configInfoVo = new ConfigInfoVo();
            configInfoVo.setId(positionInfoVo.getId());
            configInfoVo.setConfig_id(configId);
            configInfoVo.setOption_value(positionInfoVo.getOption_value());
            if (dataType == 1) {
                addList.add(configInfoVo);
            } else {
                updateList.add(configInfoVo);
            }
        }
        return msg;
    }
}
