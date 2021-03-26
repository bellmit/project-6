package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.OptionConfigConstant;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.entity.XyAdPosition;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import com.miguan.xuanyuan.mapper.XyAdPositionMapper;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 广告位
 * @Date 2021/1/21
 **/
@Service
public class XyAdPositionServiceImpl implements XyAdPositionService {

    @Resource
    private XyAdPositionMapper mapper;

    @Resource
    private RedisService redisService;

    @Resource
    private XyOptionItemConfigService xyOptionItemConfigService;
    @Resource
    private XyAppService xyAppService;

    @Resource
    StrategyGroupService strategyGroupService;
    @Resource
    UserService userService;

    @Override
    @Transactional
    public void save(XyAdPosition xyAdPosition) throws ValidateException {

        //广告位名称唯一
        int count = mapper.judgeExistName(xyAdPosition.getPositionName(),xyAdPosition.getAppId(),xyAdPosition.getId());
        if(count > 0){
            throw new ValidateException("该广告位名称已存在！");
        }
        if(xyAdPosition.getId() == null){
            String positionKey = getPositionKey();
            xyAdPosition.setPositionKey(positionKey);
            mapper.insert(xyAdPosition);
            //初始化策略默认分组
            strategyGroupService.initStrategyGroup(xyAdPosition.getId());
        } else {
            mapper.update(xyAdPosition);
        }
    }

    /**
     * @Author kangkunhuang
     * @Description 防止数据库有重复的key
     * @Date 2021/2/20
     **/
    private String getPositionKey() {
        String positionKey = StringUtil.getUUID();
        if(mapper.judgePositionKey(positionKey)>0) {
            getPositionKey();
        }
        return positionKey;
    }

    @Override
    public PageInfo<XyAdPositionVo> pageList(int plat, Long userId, String username, Integer type, String keyword, Integer clientType, Integer status, String adType, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("plat",plat);
        params.put("userId",userId);
        params.put("username",username);
        params.put("type",type);
        params.put("keyword",keyword);
        params.put("clientType",clientType);
        params.put("status",status);
        params.put("adType",adType);
        PageHelper.startPage(pageNum, pageSize);
        Page<XyAdPositionVo> pageResult = mapper.findPageList(params);
        List<XyAdPositionVo> result = pageResult.getResult();
        if(CollectionUtils.isNotEmpty(result)){
            result.forEach(r -> {
                if(r.getClientType()  != null && r.getClientType() == 1){
                    r.setClientLogo(redisService.get(RedisKeyConstant.CONFIG_CODE + RedisKeyConstant.ANDROID_LOGO));
                } else {
                    r.setClientLogo(redisService.get(RedisKeyConstant.CONFIG_CODE + RedisKeyConstant.IOS_LOGO));
                }
                if(StringUtils.isNotEmpty(r.getAdType())){
                    XyOptionItemConfig config = xyOptionItemConfigService.findByCodeAndKey(OptionConfigConstant.AD_TYPE, r.getAdType());
                    if(config != null){
                        r.setAdTypeName(config.getItemVal());
                    } else {
                        r.setAdTypeName("");
                    }
                }
            });
        }
        return new PageInfo(pageResult);
    }

    @Override
    public XyAdPosition findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
        //需要删除聚合管理的所有内容。
        strategyGroupService.deleteByPositionId(id);
    }

    @Override
    public void deleteByAppId(Long appId) {
        mapper.deleteByAppId(appId);
        //需要删除聚合管理的所有内容。
        strategyGroupService.deleteByAppId(appId);
    }

    @Override
    public List<XyAdPositionSimpleVo> findList(int plat, Long userId, Long appId) {
        return mapper.findList(plat,userId, appId);
    }

    @Override
    public List<OptionsVo> linkageSelection(int plat, Long userId) {
        return fillAppInfo(plat,userId);
    }

    private List<OptionsVo> fillAppInfo(int plat, Long userId) {
        List<XyAppSimpleVo> appList = xyAppService.findList(plat, userId);
        if(CollectionUtils.isEmpty(appList)){
            return Lists.newArrayList();
        }
        List<OptionsVo> optionsVos = Lists.newArrayList();
        appList.forEach(app -> {
            OptionsLinkageVo optionsVo = new OptionsLinkageVo(app.getId().toString(),app.getAppName());
            List<XyAdPositionSimpleVo> adPositions = findList(plat, userId,app.getId());
            List<OptionsVo> childrens = fillPositionInfo(adPositions);
            optionsVo.setChildren(childrens);
            optionsVos.add(optionsVo);
        });
        return optionsVos;
    }

    private List<OptionsVo> fillPositionInfo(List<XyAdPositionSimpleVo> adPositions) {
        if(CollectionUtils.isEmpty(adPositions)){
            return Lists.newArrayList();
        }
        List<OptionsVo> list = Lists.newArrayList();
        adPositions.forEach(adPosition -> {
            OptionsVo optionsVo = new OptionsVo(adPosition.getId().toString(),adPosition.getPositionName());
            list.add(optionsVo);
        });
        return list;
    }

    public AdPositionDetailDto getPositionDetail(Long positionId) {
        return mapper.getPositionDetail(positionId);
    }

    @Override
    public List<OptionsVo> thirdlinkageSelection(int plat) {
        List<User> userList = userService.listValidUser(plat);
        if(CollectionUtils.isEmpty(userList)){
            return Lists.newArrayList();
        }
        
        List<OptionsVo> optionsVos = Lists.newArrayList();
        userList.forEach(user -> {
            OptionsLinkageVo optionsVo = new OptionsLinkageVo(user.getId().toString(),user.getUsername());
            List<OptionsVo> childrenLinkages = fillAppInfo(plat, Long.valueOf(user.getId()));
            optionsVo.setChildren(childrenLinkages);
            optionsVos.add(optionsVo);
        });
        return optionsVos;
    }

}
