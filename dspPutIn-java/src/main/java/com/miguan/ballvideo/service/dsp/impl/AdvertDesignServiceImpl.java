package com.miguan.ballvideo.service.dsp.impl;

import cn.jiguang.common.utils.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.entity2.advert.AdvertCode;
import com.miguan.ballvideo.mapper3.AdvertDesignMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.dsp.*;
import com.miguan.ballvideo.vo.request.*;
import com.miguan.ballvideo.vo.response.AdvertDesignListRes;
import com.miguan.ballvideo.vo.response.AdvertDesignModifyRes;
import com.miguan.ballvideo.vo.response.AdvertDesignSimpleRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 创意创意
 */
@Slf4j
@Service
public class AdvertDesignServiceImpl implements AdvertDesignService {

    @Resource
    private AdvertDesignMapper advertDesignMapper;

    //其它的业务信息
    @Resource
    private AdvertDesWeightService advertDesWeightService;
    @Autowired
    private AdvertPlanService advertPlanService;
    @Autowired
    private AdvertGroupService advertGroupService;
    @Resource
    private RedisService redisService;
    @Autowired
    private AdvertCodeService advertCodeService;

    @Override
    public PageInfo<AdvertDesignListRes> pageList(String keyword, Integer advertUserId, Integer planId, Integer materialShape, String startDay, String endDay, String sort, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("keyword",keyword);
        params.put("advertUserId",advertUserId);
        params.put("planId",planId);
        params.put("materialShape",materialShape);
        params.put("startDay",startDay);
        params.put("endDay",endDay);
        params.put("sort", StringUtils.isEmpty(sort) ? "id desc" : StringUtil.humpToLine(sort));
        PageHelper.startPage(pageNum, pageSize);
        Page<AdvertDesignListRes> pageResult = advertDesignMapper.findPageList(params);
        return new PageInfo(pageResult);
    }

    @Override
    @Transactional
    public void saveBatch(AdvertDesignModifyVo advertDesignModifyVo) throws ValidateException, ServiceException {
        List<AdvertDesWeightVo> weightVos = Lists.newArrayList();
        //先删除对应的关联
        List<AdvertDesignVo> designList = advertDesignModifyVo.getDesignList();
        if(CollectionUtils.isEmpty(designList)){
            throw new ValidateException("必须至少传入一个广告创意！");
        }
        List<Long> designIds = Lists.newArrayList();
        if(advertDesignModifyVo.getEdit_design_id() == null){
            //新增操作
            for (AdvertDesignVo designVo:designList) {
                if(designVo.getId() == null){
                    advertDesignMapper.insert(designVo);
                    designIds.add(designVo.getId());
                    AdvertDesWeightVo weightVo = new AdvertDesWeightVo();
                    weightVo.setPlan_id(advertDesignModifyVo.getPlan_id());
                    weightVo.setDesign_id(designVo.getId());
                    weightVo.setGroup_id(advertDesignModifyVo.getGroup_id());
                    weightVo.setWeight(1L);//权重这个功能.目前废弃掉了。
                    weightVos.add(weightVo);
                }
            }
            if(CollectionUtils.isNotEmpty(weightVos)){
                advertDesWeightService.insertBatch(weightVos);
            }
        } else {
            //修改操作
            Long editDesignId = advertDesignModifyVo.getEdit_design_id();
            Optional<AdvertDesignVo> advertDesignVoOptional = designList.stream().filter(design -> editDesignId.equals(design.getId())).findFirst();
            if(!advertDesignVoOptional.isPresent()){
                throw new ValidateException("该创意不存在列表里面");
            }
            AdvertDesignVo designVo = advertDesignVoOptional.get();
            advertDesignMapper.update(designVo);
            designIds.add(designVo.getId());
        }
        removeAdvCache(designIds);
    }

    @Override
    public AdvertDesignVo findById(Long designId) {
        AdvertDesignVo designVo = advertDesignMapper.getById(designId);
        AdvertDesWeightVo weightVo = advertDesWeightService.findByDesignId(designId);
        if(weightVo == null){
            return designVo;
        }
        designVo.setGroup_id(weightVo.getGroup_id());
        designVo.setPlan_id(weightVo.getPlan_id());
//        designVo.setDesign_weights(String.valueOf(weightVo.getWeight()));
        return designVo;
    }

    @Override
    public AdvertDesignModifyRes findResById(Long designId) {
        AdvertDesignModifyRes modifyRes = new AdvertDesignModifyRes();
        AdvertDesWeightVo weightVo = advertDesWeightService.findByDesignId(designId);
        if(weightVo == null){
            return modifyRes;
        }
        List<AdvertDesignVo> designVos = Lists.newArrayList();
        designVos.add(advertDesignMapper.getById(weightVo.getDesign_id()));
//        weighVo.forEach(weigh -> {
//            AdvertDesignVo designVo = advertDesignMapper.getById(weigh.getDesign_id());
//            designVo.setDesign_weights(weigh.getWeight().toString());
//            designVos.add(designVo);
//        });
        modifyRes.setDesignList(designVos);
        modifyRes.setGroupVo(weightVo.getGroup_id() == null ? null : advertGroupService.getAdvertGroupById(weightVo.getGroup_id().intValue()));
        AdvertPlanVo simplePlanVo = advertPlanService.findSimpleById(weightVo.getPlan_id());
        modifyRes.setPlanVo(simplePlanVo);

        modifyRes.setGroup_id(weightVo.getGroup_id());
        modifyRes.setPlan_id(weightVo.getPlan_id());
        modifyRes.setEdit_design_id(designId);
        modifyRes.setMaterial_type(simplePlanVo.getMaterial_type());
        modifyRes.setMaterial_shape(simplePlanVo.getMaterial_shape());
        return modifyRes;
    }

    @Transactional
    @Override
    public void changeState(int state, Long id) {
        removeAdvCache(id);
        advertDesignMapper.changeState(state, id);
    }

    @Override
    public List<AdvertDesignSimpleRes> getDesignList(Long advertUserId) {
        return advertDesignMapper.getDesignList(advertUserId);
    }

    /**
     * 新增或修改广告创意
     */
    @Override
    @Transactional
    public void save(AdvertDesignVo advertDesignVo){
        if (advertDesignVo.getId() == null) {
            //新增操作
            advertDesignMapper.insert(advertDesignVo);
        } else {
            //修改操作
            advertDesignMapper.update(advertDesignVo);
        }
        removeAdvCache(advertDesignVo.getId());
    }

    @Override
    public void delete(Long id) {
        removeAdvCache(id);
        advertDesignMapper.deleteById(id);
        advertDesWeightService.deleteByDesId(id); //删除广告计划下的创意和计划的关联关系
    }

    @Override
    public List<AdvertDesignVo> getByPlanId(Long groupId) {
        return advertDesignMapper.getByPlanId(groupId);
    }

    @Override
    public void updateDesignState(Map<String, Object> params) {
        List<Long> idList = (List<Long>)params.get("idList");
        removeAdvCache(idList);
        advertDesignMapper.updateDesignState(params);
    }

    @Override
    public void updateDesignByPlanState(Map<String, Object> params) {
        if(params.get("idList") == null){
            return;
        }
        List<String> planIds = (List<String>)params.get("idList");
//        removeAdvCache(id);
        List<AdvertCodeVo> codes = advertCodeService.findAdvCodeByPlanIds(planIds);
        removeAdvCacheByCodes(codes);
        advertDesignMapper.updateDesignByPlanState(params);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        List<String> planIds = Lists.newArrayList();
        planIds.add(String.valueOf(planId));
        List<AdvertCodeVo> codes = advertCodeService.findAdvCodeByPlanIds(planIds);
        removeAdvCacheByCodes(codes);
        advertDesignMapper.deleteByPlanId(planId);
    }

    private void removeAdvCache(Long designId) {
        List<Long> designIds = Lists.newArrayList();
        designIds.add(designId);
        removeAdvCache(designIds);
    }

    private void removeAdvCache(List<Long> designIds) {
        //查询广告
        List<AdvertCodeVo> codes = advertCodeService.findAdvCodeInfoByDesignIds(designIds);
        removeAdvCacheByCodes(codes);
    }
    @Override
    public void removeAdvCacheByCodes(List<AdvertCodeVo> codes) {
        //查询广告
        codes.forEach(code -> {
            String key = RedisKeyConstant.GET_ADV_CACHE + "?appId=" +code.getApp_id() + "&code=" + code.getCode_id() + "&*";
            Set<String> delKeys = redisService.getScan(key, 100);
            delKeys.forEach(matchkey -> {redisService.del(matchkey);});
        });
    }
}
