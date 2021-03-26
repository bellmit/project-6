package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.mapper.FlowTestMapper;
import com.miguan.advert.domain.mapper.MigratingDataMapper;
import com.miguan.advert.domain.mapper.PositionInfoMapper;
import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.pojo.AdAdvertTestConfig;
import com.miguan.advert.domain.pojo.AdTestCodeRelation;
import com.miguan.advert.domain.service.AbFlowGroupService;
import com.miguan.advert.domain.service.MigratingDataService;
import com.miguan.advert.domain.vo.result.AdvertConfigAndCodeVo;
import com.miguan.advert.domain.vo.result.AppAdPositionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MigratingDataServiceImpl implements MigratingDataService {



    @Resource
    private MigratingDataMapper migratingDataMapper;

    @Resource
    private FlowTestMapper flowTestMapper;


    @Resource
    private PositionInfoMapper positionInfoMapper;

    @Resource
    private AbFlowGroupService abflowGroupService;

    @Override
    public ResultMap migratingAdConfigData() {
        try {

            //删除流量分组表
            migratingDataMapper.deleteFlow();
            //删除测试分组表
            migratingDataMapper.deleteTest();
            //删除测试与代码位关联表
            migratingDataMapper.deleteConfig();
            // 查出所有广告配置信息
            List<AdvertConfigAndCodeVo> allAdAdvertConfigList = migratingDataMapper.getAllAdAdvertConfigList();
            // 查出所有广告位关联配置信息
            List<AdvertConfigAndCodeVo> allAdAdvertConfigCodeList = migratingDataMapper.getAllAdAdvertConfigCodeList();
            //新增默认分组
            saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "默认分组");
            //新增AB默认分组
            saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "AB默认分组");
            return new ResultMap("执行成功！");
        } catch (Exception e) {
            log.error("广告配置信息数据迁移出现异常：" + e.getMessage());
            return new ResultMap("出现异常！异常信息：" + e.getMessage());
        }
    }


    //新增的逻辑
    @Override
    public ResultMap migratingAdConfigDataDefalut() {
        try {
            //查询实验信息
            List<Integer> abIds = migratingDataMapper.searchWithoutDefault();
            //删除流量分组表
            migratingDataMapper.deleteFlow();
            //删除测试分组表
            migratingDataMapper.deleteTest();
            //删除测试与代码位关联表
            migratingDataMapper.deleteConfig();
            // 查出所有广告配置信息
            List<AdvertConfigAndCodeVo> allAdAdvertConfigList = migratingDataMapper.getAllAdAdvertConfigList();
            // 查出所有广告位关联配置信息
            List<AdvertConfigAndCodeVo> allAdAdvertConfigCodeList = migratingDataMapper.getAllAdAdvertConfigCodeList();
            //新增默认分组
            saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "默认分组");
            //新增AB默认分组
            //saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "AB默认分组");
            return new ResultMap("执行成功！");
        } catch (Exception e) {
            log.error("广告配置信息数据迁移出现异常：" + e.getMessage());
            return new ResultMap("出现异常！异常信息：" + e.getMessage());
        }
    }


    /**
     * 每个广告位保存一个分组,有默认走默认。没默认。走实验  删除的逻辑
     * @return
     */
    @Transactional
    @Override
    public ResultMap moveAbConfigData(Integer bakPositionId,boolean b) {
        try {

            List<AdAdvertFlowConfig> config = migratingDataMapper.findFlowConfigWithAb();
            //查询仅存在AB默认，而不存在默认。
            List<Integer> abIds = migratingDataMapper.searchWithoutDefault();
            if(b){
                //如果abIds 为空。
                if(CollectionUtils.isEmpty(abIds) && b){
                    //删除测试与代码位关联表
                    migratingDataMapper.deleteAbConfigEpt();
                    //删除测试分组表
                    migratingDataMapper.deleteAbTestEpt();
                    //删除流量分组表
                    migratingDataMapper.deleteAbFlowEpt();
                } else {
                    //删除测试与代码位关联表
                    migratingDataMapper.deleteAbConfig(abIds,null);
                    //删除测试分组表
                    migratingDataMapper.deleteAbTest(abIds,null);
                    //删除流量分组表
                    migratingDataMapper.deleteAbFlow(abIds,null);

                    //将流量信息置空
                    migratingDataMapper.updateAbFlowInfo(null);
                    migratingDataMapper.updateAbTestInfo(null);
                }
                //删除实验信息  该过程 是删除AB实验的信息
//                config.forEach(c ->{
//                    try {
//                        if(StringUtils.isNotEmpty(c.getAbFlowId())){
//                            Integer positionId = c.getPositionId();
//                            AppAdPositionVo position = positionInfoMapper.getPositionById(positionId);
//                            String appPackage = position.getApp_package();
//                            abflowGroupService.sendEditState(appPackage,4,c.getAbFlowId(),null);
//                        }
//                    } catch (Exception e){
//                        //错误不做处理
//                    }
//                });
            } else {
                if(bakPositionId == null || bakPositionId <= 0){
                    return ResultMap.error("必须输入广告位");
                }
                if(CollectionUtils.isEmpty(abIds)){
                    return ResultMap.error("没有启动");
                }

                //删除测试与代码位关联表
                migratingDataMapper.deleteAbConfig(abIds,bakPositionId);
                //删除测试分组表
                migratingDataMapper.deleteAbTest(abIds,bakPositionId);
                //删除流量分组表
                migratingDataMapper.deleteAbFlow(abIds,bakPositionId);

                //将流量信息置空
                migratingDataMapper.updateAbTestInfo(bakPositionId);
                migratingDataMapper.updateAbFlowInfo(bakPositionId);
                //删除实验信息  该过程 是删除AB实验的信息
//                config.forEach(c ->{
//                    try {
//                        if(StringUtils.isNotEmpty(c.getAbFlowId())){
//                            Integer positionId = c.getPositionId();
//                            if(positionId == bakPositionId){
//                                AppAdPositionVo position = positionInfoMapper.getPositionById(positionId);
//                                String appPackage = position.getApp_package();
//                                abflowGroupService.sendEditState(appPackage,4,c.getAbFlowId(),null);
//                            }
//                        }
//                    } catch (Exception e){
//                        //错误不做处理
//                    }
//                });
            }


//            // 查出所有广告配置信息
//            List<AdvertConfigAndCodeVo> allAdAdvertConfigList = migratingDataMapper.getAllAdAdvertConfigList();
//            // 查出所有广告位关联配置信息
//            List<AdvertConfigAndCodeVo> allAdAdvertConfigCodeList = migratingDataMapper.getAllAdAdvertConfigCodeList();
//            //新增默认分组
//            saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "默认分组");
//            //新增AB默认分组
//            saveFlowTest(allAdAdvertConfigList, allAdAdvertConfigCodeList, "AB默认分组");
            return new ResultMap("执行成功！");
        } catch (Exception e) {
            log.error("广告配置信息数据迁移出现异常：" + e.getMessage());
            return new ResultMap("出现异常！异常信息：" + e.getMessage());
        }
    }

    /**
     * 每个广告位保存一个分组,有默认走默认。没默认。走实验  删除的逻辑
     * @return
     */
    @Transactional
    @Override
    public ResultMap rollback() {


        //删除流量分组表
        migratingDataMapper.deleteFlow();
        //删除测试分组表
        migratingDataMapper.deleteTest();
        //删除测试与代码位关联表
        migratingDataMapper.deleteConfig();


        //删除流量分组表
        migratingDataMapper.copyFlow();
        //删除测试分组表
        migratingDataMapper.copyTest();
        //删除测试与代码位关联表
        migratingDataMapper.copyConfig();

        return ResultMap.success();
    }


    private void saveFlowTest(List<AdvertConfigAndCodeVo> allAdAdvertConfigList, List<AdvertConfigAndCodeVo> allAdAdvertConfigCodeList,
                              String flowName) {
        // 初始化批量插入数据List
        // 广告流量组List
        List<AdAdvertFlowConfig> adAdvertFlowConfigList = new ArrayList<>();
        // 广告实验组List
        List<AdAdvertTestConfig> adAdvertTestConfigList = new ArrayList<>();
        // 广告实验组与代码位关联List
        List<AdTestCodeRelation> adTestCodeRelationList = new ArrayList<>();

        // 流量组主键id
        Integer adAdvertFlowId = migratingDataMapper.getAdAdvertFlowConfigCount() + 1;
        // 实验组主键id
        Integer adAdvertTestId = migratingDataMapper.getAdAdvertTestConfigCount() + 1;

        Integer testState = 1;

        //如果是默认分组，关闭实验组状态
        if(flowName.equals("默认分组")){
            testState = 0;
        }

        // 循环 所有广告配置信息
        for (AdvertConfigAndCodeVo adAdvertConfig : allAdAdvertConfigList) {
            // 组装广告流量组
            AdAdvertFlowConfig adAdvertFlowConfig = new AdAdvertFlowConfig();
            adAdvertFlowConfig.setId(adAdvertFlowId);
            adAdvertFlowConfig.setName(flowName);
            //adAdvertFlowConfig.setAb_flow_id("");
            adAdvertFlowConfig.setPosition_id(Integer.parseInt(adAdvertConfig.getPositionId()));
            if(adAdvertConfig.getState() == 0){
                continue;
            }
            adAdvertFlowConfig.setState(adAdvertConfig.getState());
            adAdvertFlowConfig.setTest_state(testState);
            adAdvertFlowConfig.setType(1);
            adAdvertFlowConfig.setCreated_at(adAdvertConfig.getCreatedAt());

            // 加入广告流量组List
            adAdvertFlowConfigList.add(adAdvertFlowConfig);

            // 组装广告实验组
            AdAdvertTestConfig adAdvertTestConfig = new AdAdvertTestConfig();
            adAdvertTestConfig.setId(adAdvertTestId);
            adAdvertTestConfig.setFlow_id(adAdvertFlowId.toString());
            adAdvertTestConfig.setComputer(adAdvertConfig.getComputer());
            adAdvertTestConfig.setType(1);
            adAdvertTestConfig.setCreated_at(adAdvertConfig.getCreatedAt());
            adAdvertTestConfig.setState(adAdvertConfig.getState());

            // 加入实验组List
            adAdvertTestConfigList.add(adAdvertTestConfig);

            // 筛选出与当前广告配置相关联的广告位List
            List<AdvertConfigAndCodeVo> relationAdAdvertConfigCodeList = allAdAdvertConfigCodeList.stream()
                    .filter(advertConfigAndCodeVo -> advertConfigAndCodeVo.getConfigId().equals(adAdvertConfig.getId().toString())).collect(Collectors.toList());

            // 循环 筛选出与当前广告配置相关联的广告位List
            for (AdvertConfigAndCodeVo adAdvertConfigCode : relationAdAdvertConfigCodeList) {
                // 组装广告实验组与代码位关联List
                AdTestCodeRelation adTestCodeRelation = new AdTestCodeRelation();
                adTestCodeRelation.setConfig_id(adAdvertTestId.toString());
                adTestCodeRelation.setCode_id(adAdvertConfigCode.getCodeId());
                adTestCodeRelation.setState(adAdvertConfig.getState());
                adTestCodeRelation.setCreated_at(adAdvertConfig.getCreatedAt());
                // 算法：1-概率补充；2-手动排序
                if (adAdvertConfig.getComputer() == 1) {
                    adTestCodeRelation.setMatching(adAdvertConfigCode.getOptionValue());
                    adTestCodeRelation.setNumber(1);
                    adTestCodeRelation.setOrder_num(0);
                } else {
                    adTestCodeRelation.setMatching(0);
                    adTestCodeRelation.setNumber(0);
                    adTestCodeRelation.setOrder_num(adAdvertConfigCode.getOptionValue());
                }

                // 加入广告实验组与代码位关联List
                adTestCodeRelationList.add(adTestCodeRelation);
            }
            adAdvertFlowId += 1;
            adAdvertTestId += 1;
        }
        log.info("adAdvertFlowConfigList需批量插入条数:" + adAdvertFlowConfigList.size());
        log.info("adAdvertTestConfigList需批量插入条数:" + adAdvertTestConfigList.size());
        log.info("adTestCodeRelationList需批量插入条数:" + adTestCodeRelationList.size());
        // 批量插入
        migratingDataMapper.insertBatchAdAdvertFlowConfig(adAdvertFlowConfigList);
        migratingDataMapper.insertBatchAdAdvertTestConfig(adAdvertTestConfigList);
        migratingDataMapper.insertBatchAdTestCodeRelation(adTestCodeRelationList);
    }
}
