package com.miguan.xuanyuan.service.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.dto.XyStrategyCodeDto;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.dto.request.SourceAppRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyPlat;
import com.miguan.xuanyuan.entity.XyPlatAccount;
import com.miguan.xuanyuan.entity.XySourceApp;
import com.miguan.xuanyuan.mapper.XyAdCodeMapper;
import com.miguan.xuanyuan.mapper.XySourceAppMapper;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.vo.AdCodeVo;
import com.miguan.xuanyuan.vo.RelateAppInfoVo;
import com.miguan.xuanyuan.vo.StrategyCodeVo;
import com.miguan.xuanyuan.vo.sdk.SourceAppInfoVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XySourceAppServiceImpl implements XySourceAppService {

    @Resource
    public XySourceAppMapper mapper;
    @Resource
    XyAdCodeService XyAdCodeService;

    public int insert(SourceAppRequest sourceAppRequest) {
        return mapper.insert(sourceAppRequest);
    }

    public int update(SourceAppRequest sourceAppRequest) throws ServiceException {
        if (sourceAppRequest.getAppId() == null && sourceAppRequest.getPlatId() == null && sourceAppRequest.getSourceAppId() == null) {
            throw new ServiceException("数据错误");
        }
        return mapper.update(sourceAppRequest);
    }

    public XySourceApp findById(Long id) {
        return mapper.findById(id);
    }

    public XySourceApp getDataByAppIdAndPlatId(Long appId, Long platId) {
        return mapper.getDataByAppIdAndPlatId(appId, platId);
    }


    private void judgeExistSourceApp(SourceAppRequest sourceAppRequest) throws ServiceException{
        int count = mapper.judgeExistSourceApp(sourceAppRequest);
        if(count > 0){
            throw new ServiceException("该广告平台应用ID,已有人使用,请重新输入。");
        }
    }

    /**
     * 设置应用对应的第三方平台应用关联关系
     *
     * @param appId
     * @param platId
     * @param sourceAppId
     * @throws ServiceException
     */
    public void putSourceApp(Long appId, Long platId, String sourceAppId) throws ServiceException {
        if (appId == null || platId == null) {
            throw new ServiceException("参数不能为空");
        }
        if(StringUtils.isEmpty(sourceAppId)){
            throw new ServiceException("广告平台应用ID不能为空");
        }
        XySourceApp sourceAppInfo = this.getDataByAppIdAndPlatId(appId, platId);
        SourceAppRequest sourceAppRequest = new SourceAppRequest();
        sourceAppRequest.setAppId(appId);
        sourceAppRequest.setPlatId(platId);
        sourceAppRequest.setSourceAppId(sourceAppId);
        judgeExistSourceApp(sourceAppRequest);
        if (sourceAppInfo != null && sourceAppInfo.getId() != null) {
            sourceAppRequest.setId(sourceAppInfo.getId());
            update(sourceAppRequest);
        } else {
            insert(sourceAppRequest);
        }

    }

    @Override
    public List<RelateAppInfoVo> relateAppInfo(Long appId) {
        return mapper.findRelateApp(appId);
    }

    @Override
    public void saveRelateApp(List<RelateAppInfoVo> relationInfos) throws ValidateException {
        List<RelateAppInfoVo> relationInfoList = judgeUseSourceApp(relationInfos);
        if(CollectionUtils.isEmpty(relationInfoList)){
            return ;
        }
        mapper.saveRelateApp(relationInfoList);
    }

    private List<RelateAppInfoVo> judgeUseSourceApp(List<RelateAppInfoVo> relationInfos) throws ValidateException{
        List<RelateAppInfoVo> appInfoVos = relationInfos.stream().filter(relateAppInfo -> !(relateAppInfo.getId() == null && StringUtils.isEmpty(relateAppInfo.getSourceAppId()))).collect(Collectors.toList());
        for (RelateAppInfoVo relateAppInfo:relationInfos) {
            if(relateAppInfo.getId() != null && StringUtils.isEmpty(relateAppInfo.getSourceAppId())){
                if(CollectionUtils.isNotEmpty(XyAdCodeService.findByAppIdAndPlatKey(relateAppInfo.getAppId(),relateAppInfo.getPlatKey()))){
                    throw new ValidateException("已存在对应的广告源,不能修改为空值！");
                }
            }
        }
        return appInfoVos;
    }

    @Override
    public String findSourceAppId(Long appId, String platKey) {
        return mapper.findSourceAppId(appId,platKey);
    }

    @Override
    public String findSourceAppByPositionIdAndPlatKey(Long positionId, String platKey) {
        return mapper.findSourceAppByPositionIdAndPlatKey(positionId,platKey);
    }

    @Override
    public List<SourceAppInfoVo> findAppInfo(Long appId) {
        return mapper.findAppInfo(appId);
    }

    @Override
    public void createInnerApp(Long appId) {
        List<XyPlat> platList = mapper.findInnerApp(appId);
        if(CollectionUtils.isEmpty(platList)){
            return ;
        }
        platList.stream().forEach(plat -> {
            SourceAppRequest sourceAppRequest = new SourceAppRequest();
            sourceAppRequest.setAppId(appId);
            sourceAppRequest.setPlatId(plat.getId());
            sourceAppRequest.setSourceAppId(XyConstant.XUANYUAN_DEFAULT_APP_ID);
            insert(sourceAppRequest);
        });
    }
}
