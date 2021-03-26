package com.miguan.ballvideo.service.dsp.impl;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.TimeConfigFormat;
import com.miguan.ballvideo.common.util.dsp.DspConstant;
import com.miguan.ballvideo.common.util.dsp.DspGlobal;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.mapper3.AdvertPlanMapper;
import com.miguan.ballvideo.service.dsp.*;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import com.miguan.ballvideo.vo.request.*;
import com.miguan.ballvideo.vo.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计划计划
 */
@Slf4j
@Service
public class AdvertPlanServiceImpl implements AdvertPlanService {

    @Resource
    private AdvertPlanMapper advertPlanMapper;

    //其它的业务信息
    @Resource
    AdvertGroupService advertGroupService;
    @Resource
    AdvertDesWeightService advertDesWeightService;
    @Resource
    AdvertDesignService advertDesignService;
    @Resource
    AdvertAccountService advertAccountService;
    @Resource
    AdvertAreaService advertAreaService;
    @Resource
    AdvertPhoneService advertPhoneService;
    @Resource
    AdvertCodeService advertCodeService;
    @Resource
    PlanCodeRelationService planCodeRelationService;
    @Resource
    private AdvertDspService advertDspService;
    @Resource
    private RestTemplate restTemplate;


    //统计近7天每个时间段的日活数占比 接口url
    @Value("${bigdata-server.get-plan-consumption}")
    private String getPlanConsumptionUrl;


    @Override
    public PageInfo<AdvertPlanListRes> pageList(String keyword, Integer advertUserId, Integer state, Integer putInType, String startDay, String endDay, String sort, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("keyword",keyword);
        params.put("advertUserId",advertUserId);
        params.put("state",state);
        params.put("putInType",putInType);
        params.put("startDay",startDay);
        params.put("endDay",endDay);
        params.put("sort", StringUtils.isEmpty(sort) ? "id desc" : StringUtil.humpToLine(sort));
        PageHelper.startPage(pageNum, pageSize);
        Page<AdvertPlanListRes> pageResult = advertPlanMapper.findPageList(params);
        return new PageInfo(pageResult);
    }

    @Override
    public PageInfo<AdvertPlanListExt> extPageList(String keyword, String advertUserName, Integer state, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("keyword",keyword);
        params.put("advertUserName",advertUserName);
        params.put("state",state);
//        params.put("sort", StringUtil.humpToLine(sort));
        PageHelper.startPage(pageNum, pageSize);
        Page<AdvertPlanListExt> pageResult = advertPlanMapper.findExtPageList(params);
        List<AdvertPlanListExt> result = pageResult.getResult();
        result.forEach(planExt -> {
            if(planExt.getMaterial_shape() != null){
                planExt.setMaterial_shape_name(MaterialShapeConstants.materialShapeNameMap.get(planExt.getMaterial_shape()));
            } else {
                planExt.setMaterial_shape_name("");
            }
            Long planId = planExt.getId();
            List<AdvertPlanCodeRelationVo> relationVo = planCodeRelationService.findByPlanId(planId);
            if(CollectionUtils.isEmpty(relationVo)){
                planExt.setPoint_adv_names("");
                return;
            }
            List<Long> codeIds = relationVo.stream().map(AdvertPlanCodeRelationVo::getCode_id).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(codeIds)){
                planExt.setPoint_adv_names("");
                return;
            }
            List<AdvertCodeSimpleRes> advertCodeVos = advertCodeService.findByCodeIds(codeIds);
            List<String> names = advertCodeVos.stream().filter(advertCodeVo -> StringUtils.isNotEmpty(advertCodeVo.getApp_position_name()))
                                    .map(AdvertCodeSimpleRes::getApp_position_name).collect(Collectors.toList());
            planExt.setPoint_adv_names(StringUtil.toString(names,"/"));
        });
        return new PageInfo(pageResult);
    }

    @Override
    public List<AdvertCodeSimpleRes> getCodeByPlanId(Long planId) {
        List<AdvertPlanCodeRelationVo> relationVo = planCodeRelationService.findByPlanId(planId);
        if(CollectionUtils.isEmpty(relationVo)){
            return Lists.newArrayList();
        }
        List<Long> codeIds = relationVo.stream().map(AdvertPlanCodeRelationVo::getCode_id).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(codeIds)){
            return Lists.newArrayList();
        }
        return advertCodeService.findByCodeIds(codeIds);
    }

    @Transactional
    @Override
    public void saveRelationCode(Long planId, String codeIds) {
        planCodeRelationService.deleteByPlanId(planId);
        List<AdvertPlanCodeRelationVo> relation = Lists.newArrayList();
        Arrays.asList(codeIds.split(",")).forEach(codeId -> {
            AdvertPlanCodeRelationVo vo = new AdvertPlanCodeRelationVo();
            vo.setPlan_id(planId);
            vo.setCode_id(Long.valueOf(codeId));
            relation.add(vo);
        });
        planCodeRelationService.insertBatch(relation);
    }


    @Override
    public List<AdvertPlanSimpleRes> getPlanList(Long advertUserId, Long groupId) {
        return advertPlanMapper.getPlanList(advertUserId, groupId);
    }

    @Override
    public void updateMaterial(Long planId, Integer materialType, Integer materialShape) {
        advertPlanMapper.updateMaterial(planId, materialType,materialShape);
    }

    @Override
    public AdvertPlanVo findSimpleById(Long planId) {
        return advertPlanMapper.getById(planId);
    }


    /**
     * 新增或修改广告计划
     */
    @Transactional
    @Override
    public void save(AdvertPlanVo advertPlanVo) throws ServiceException{
        if (advertPlanVo == null) {
            throw new ServiceException("广告计划信息为空");
        }
        //添加计划组
        double dayGroupPrice = 0D; //组预算
        if(advertPlanVo.getGroup().getId() == null){
            advertGroupService.saveGroup(advertPlanVo.getGroup());
            dayGroupPrice = advertPlanVo.getGroup().getDayPrice(); //组预算
        } else {
            AdvertGroupVo groupVo = advertGroupService.getAdvertGroupById(advertPlanVo.getGroup().getId());
            dayGroupPrice = groupVo.getDayPrice(); //组预算
        }
        advertPlanVo.setGroup_id(advertPlanVo.getGroup().getId());

        boolean isNewPlan = false; //是否新增计划
        if (advertPlanVo.getId() == null) {
            //新增操作
            //控量
            int thresholdValue = DspGlobal.getInt(DspConstant.SMOOTH_THRESHOLD_VALUE);
            if(advertPlanVo.getDay_price().doubleValue() <= thresholdValue){
                advertPlanVo.setSmooth_date(new Date());
            } else {
                advertPlanVo.setSmooth_date(null);
            }
            advertPlanMapper.insert(advertPlanVo);
            isNewPlan = true;
        } else {
            //修改前先进行校验 账户金额
            AdvertPlanVo oldPlanVo = advertPlanMapper.getById(advertPlanVo.getId());
            triggerAccount(advertPlanVo.getId(),oldPlanVo,advertPlanVo);
            //修改操作
            advertPlanMapper.update(advertPlanVo);
        }
        Long planId = advertPlanVo.getId();
        //删除
        advertAreaService.deleteByPlanId(planId);
        advertPhoneService.deleteByPlanId(planId);
        //保存
        saveArea(advertPlanVo);
        savePhone(advertPlanVo);
        //新增和修改创意信息 。其它操作不管
//        advertDesWeightService.deleteByPlanId(planId);
        saveDesign(advertPlanVo);
        //新创建的计划的话，则需要同时创建计划账号表
        if(isNewPlan) {
            advertDspService.createdPlanAccount(planId.intValue(), advertPlanVo.getDay_price().doubleValue(), dayGroupPrice, advertPlanVo.getTotal_price().doubleValue());
        }

        //关联对应的代码位,自动匹配代码位。
        if(isNewPlan){
            String size = MaterialShapeConstants.materialShapeMap.get(advertPlanVo.getMaterial_shape());
            List<AdvertCodeVo> codeList = advertCodeService.findByStyleSize(size,advertPlanVo.getMaterial_type());
            planCodeRelationService.deleteByPlanId(advertPlanVo.getId());
            if(CollectionUtils.isNotEmpty(codeList)){
                if(codeList.get(0) != null && StringUtils.isNotEmpty(codeList.get(0).getAdvert_type())){
                    advertPlanMapper.updateAdvType(advertPlanVo.getId(),codeList.get(0).getAdvert_type());
                }
                List<AdvertPlanCodeRelationVo> relation = Lists.newArrayList();
                codeList.forEach(code -> {
                    AdvertPlanCodeRelationVo vo = new AdvertPlanCodeRelationVo();
                    vo.setPlan_id(advertPlanVo.getId());
                    vo.setCode_id(code.getId());
                    relation.add(vo);
                });
                planCodeRelationService.insertBatch(relation);
            }
        }
    }

    private void saveArea(AdvertPlanVo planVo) {
        AdvertAreaVo advertAreaVo = new AdvertAreaVo();
        advertAreaVo.setPlan_id(planVo.getId());
        advertAreaVo.setArea(planVo.getArea());
        advertAreaVo.setType(planVo.getArea_type());
        advertAreaService.insert(advertAreaVo);
    }
    private void savePhone(AdvertPlanVo planVo) {
        AdvertPhoneVo advertPhoneVo = new AdvertPhoneVo();
        advertPhoneVo.setPlan_id(planVo.getId());
        advertPhoneVo.setType(planVo.getPhone_type());
        advertPhoneVo.setBrand(planVo.getBrand());
        advertPhoneService.insert(advertPhoneVo);
    }
    private void saveDesign(AdvertPlanVo planVo) throws ServiceException {
        List<AdvertDesignVo> designList = planVo.getDesign_list();
        if(CollectionUtils.isEmpty(designList)){
            throw new ServiceException("至少填写一个广告创意");
        }
        List<AdvertDesWeightVo> advertDesWeightVos = Lists.newArrayList();
        designList.stream().forEach(designVo ->{
            Integer materialType = MaterialShapeConstants.oldMaterialTypeMap.get(planVo.getMaterial_shape());
            designVo.setMaterial_type(materialType);
            AdvertDesWeightVo weightVo = new AdvertDesWeightVo();
            if(designVo.getId() == null){
                advertDesWeightVos.add(weightVo);
            }
            advertDesignService.save(designVo);
            weightVo.setPlan_id(planVo.getId());
            weightVo.setDesign_id(Long.valueOf(designVo.getId()));
            weightVo.setGroup_id(planVo.getGroup_id().longValue());
            weightVo.setWeight(1L); //权重目前不用了
        });
        advertDesWeightService.insertBatch(advertDesWeightVos);
    }

    /**
     * 修改金额。 （校验），根据PHP的逻辑处理
     * @param id
     * @param oldPlanVo
     * @param newPlanVo
     */
    private void triggerAccount(Long id, AdvertPlanVo oldPlanVo, AdvertPlanVo newPlanVo) throws ServiceException {
        BigDecimal subDayNum = newPlanVo.getDay_price().subtract(oldPlanVo.getDay_price());
        BigDecimal subTotalNum = newPlanVo.getTotal_price().subtract(oldPlanVo.getTotal_price());
        log.info("计划id[{}],修改前的日预算[{}],修改后的日预算[{}],修改前的总预算[{}],修改后的总预算[{}]",id,oldPlanVo.getDay_price().doubleValue(),
                newPlanVo.getDay_price().doubleValue(),oldPlanVo.getTotal_price().doubleValue(),newPlanVo.getTotal_price().doubleValue());

        boolean flag = true;
        AdvertAccountVo accountVo = advertAccountService.findOneByPlanId(id);
        //校验预算表
        if(accountVo != null){
            boolean validateDayFlag = false; //在每日日预算重置时。账户日预算拿到的是剩余总预算。
            BigDecimal todayConsumption = null;
            if((accountVo.getRemain_total_price().doubleValue() == accountVo.getRemain_day_price().doubleValue())
                    && oldPlanVo.getTotal_price().doubleValue() != 0){
                todayConsumption = getConsumption(id);
                if(todayConsumption.subtract(oldPlanVo.getDay_price()).doubleValue() > 0){
                    todayConsumption = oldPlanVo.getDay_price();
                }
                validateDayFlag = true ;
            }

            BigDecimal newRemainDayPrice = accountVo.getRemain_day_price().add(subDayNum);
            if( !validateDayFlag && newRemainDayPrice.doubleValue() < 0){
                throw new ServiceException("剩余日预算小于0,（还剩余"+accountVo.getRemain_day_price()+"），请重新修改。");
            } else if (todayConsumption != null && (newPlanVo.getDay_price().subtract(todayConsumption).doubleValue()) < 0 ){
                throw new ServiceException("剩余日预算小于0,（还剩余"+oldPlanVo.getDay_price().subtract(todayConsumption).doubleValue()+"），请重新修改。");
            }
            BigDecimal newRemainTotalPrice = accountVo.getRemain_total_price().add(subTotalNum);
            if(newRemainTotalPrice.doubleValue() < 0){
                throw new ServiceException("剩余总预算小于0,（还剩余"+accountVo.getRemain_total_price()+"），请重新修改。");
            }
            //获取今日已用的金额
            if(validateDayFlag){
                //可能会有差价 总价格少计算了些。或者多计算了些
                BigDecimal newDayPriceTmp = newPlanVo.getDay_price().subtract(todayConsumption);
                //重新计算日价格  拿到两者最小 总价值和 剩余日价值
                if(newRemainTotalPrice.doubleValue() < newDayPriceTmp.doubleValue()){
                    newRemainDayPrice = newRemainTotalPrice;
                } else {
                    newRemainDayPrice = newDayPriceTmp;
                    newRemainTotalPrice = newRemainDayPrice.subtract(subDayNum).add(subTotalNum); //确保总金额与日金额一致
                }
//                newRemainDayPrice = new BigDecimal(Math.min(newRemainTotalPrice.doubleValue(),newPlanVo.getDay_price().subtract(todayConsumption).doubleValue()));

            }
            //除了总价为0时,日价格不能大于总价格
            if(oldPlanVo.getTotal_price().doubleValue() != 0 && newRemainDayPrice.doubleValue() > newRemainTotalPrice.doubleValue()){
                newRemainDayPrice = newRemainTotalPrice;
            }

            log.info("计划id[{}],修改后的剩余日预算[{}],修改后的剩余总预算[{}]",id,newRemainDayPrice.doubleValue(),newRemainTotalPrice.doubleValue());
            accountVo.setRemain_day_price(newRemainDayPrice);
            accountVo.setRemain_total_price(newRemainTotalPrice);
            advertAccountService.update(accountVo);
            //控量
            int thresholdValue = DspGlobal.getInt(DspConstant.SMOOTH_THRESHOLD_VALUE);
            if(accountVo.getRemain_day_price().doubleValue() <= thresholdValue){
                advertPlanMapper.updateSmoothDate(new Date(),id);
            } else {
                advertPlanMapper.updateSmoothDate(null,id);
            }
        }
        if(!flag){
            throw new ServiceException("预算更新失败");
        }

    }

    /**
     * 从大数据端,获得今天已经消耗的金额
     * @param id
     * @return
     */
    private BigDecimal getConsumption(Long id) {
        String url = getPlanConsumptionUrl + "?planId=" + id;
        String resultJson = restTemplate.getForObject(url, String.class);
        ResultMap resultMap = JSONObject.parseObject(resultJson, ResultMap.class);
        BigDecimal consumption = null;
        if(resultMap == null || resultMap.getData() == null){
            consumption = new BigDecimal(0);
        } else {
            consumption = new BigDecimal(resultMap.getData().toString());
        }
        log.info("从大数据端获取计划[{}],今日已消耗的价格为[{}]",id,consumption.doubleValue());
        return consumption;
    }

    /**
     * 删除广告计划
     * @param id
     */
    @Transactional
    @Override
    public void delete(Long id) {
        advertPlanMapper.deleteById(id);
        advertDesignService.deleteByPlanId(id); //删除广告计划下的创意
        advertDesWeightService.deleteByPlanId(id); //删除广告计划下的创意和计划的关联关系
        planCodeRelationService.deleteByPlanId(id);
    }

    @Override
    public List<AdvertPlanVo> getByGroupId(Long groupId) {
        return advertPlanMapper.getByGroupId(groupId);
    }

    /**
     * 计划组批量上线 和 批量下线
     * @param state 类型，1--批量上线，0--批量下线
     * @param ids 计划组id，多个逗号分隔
     */
    public void batchOnlineAndUnderline(int state, String ids) {
        if(StringUtils.isBlank(ids)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();

        List<String> idList = Arrays.asList(ids.split(","));
        params.put("idList", idList);
        params.put("state", state);
        advertPlanMapper.updatePlanState(params);  //批量修改计划的状态
        advertDesignService.updateDesignByPlanState(params);  //批量修改创意的状态
    }

    @Override
    public AdvertPlanRes getPlanInfo(Long planId) throws ServiceException {
        AdvertPlanRes advertPlanRes = advertPlanMapper.findResById(planId);
        if(advertPlanRes == null){
            throw new ServiceException("计划不存在.");
        }
        advertPlanRes.setPosition_type(advertPlanRes.getAdvert_type());
        AdvertAreaVo areaVo = advertAreaService.findByPlanId(planId);
        if(areaVo != null){
            advertPlanRes.setArea_type(areaVo.getType());
            List<DistrictRes> districtList = advertAreaService.findAreaInfo(areaVo);
            advertPlanRes.setArea(districtList);
        }
        AdvertPhoneVo phoneVo = advertPhoneService.findByPlanId(planId);
        if(phoneVo != null){
            advertPlanRes.setPhone_type(phoneVo.getType());
            advertPlanRes.setPhone(advertPhoneService.findPhoneInfo(phoneVo));
        }
        List<AdvertDesWeightVo> weightVos = advertDesWeightService.findByPlanId(planId);
        List<AdvertDesignVo> designVos = Lists.newArrayList();
        weightVos.forEach(weightVo -> {
            AdvertDesignVo designVo = advertDesignService.findById(weightVo.getDesign_id());
            designVos.add(designVo);
        });
        advertPlanRes.setDesign_list(designVos);
        advertPlanRes.setGroup(advertPlanRes.getGroup_id() == null ? null : advertGroupService.getAdvertGroupById(advertPlanRes.getGroup_id()));
        //多个时段
        advertPlanRes.setTimes_config(TimeConfigFormat.reanalysisTimeConfig(advertPlanRes.getTime_setting(),advertPlanRes.getTmp_times_config()));
        return advertPlanRes;
    }

}