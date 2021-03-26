package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.XyUtil;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.entity.*;
import com.miguan.xuanyuan.mapper.AdminPermissionMapper;
import com.miguan.xuanyuan.mapper.XyPlanPositionCodeMapper;
import com.miguan.xuanyuan.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 轩辕品牌广告代码位表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-18
 */
@Service
public class XyPlanPositionCodeServiceImpl extends ServiceImpl<XyPlanPositionCodeMapper, XyPlanPositionCode> implements XyPlanPositionCodeService {

    @Resource
    private XyPlanPositionCodeMapper mapper;

    @Resource
    XyAdPositionService positionService;

    @Resource
    XyAdCodeService xyAdCodeService;

    @Resource
    XyStrategyService xyStrategyService;

    @Resource
    XyStrategyCodeService xyStrategyCodeService;

    /**
     * 根据广告位获取广告计划代码位
     * @param positionId
     * @return
     */
    public XyPlanPositionCode getPlanPositionCode(Long positionId) {
        return mapper.getPlanPositionCode(positionId);
    }

    /**
     * 广告计划品牌广告广告位对应的代码位
     *
     * @param positionId
     * @throws ServiceException
     */
    public void addPositionPlanCodeId(long positionId) throws ServiceException {
        XyAdPosition positionInfo = positionService.findById(positionId);
        if (positionInfo == null) {
            throw new ServiceException("广告位不存在");
        }

        XyPlanPositionCode planPositionCode = this.getPlanPositionCode(positionId);
        if (planPositionCode == null) {
            String codeId = this.getPlanCodeId(positionId, positionInfo.getAdType());
            planPositionCode = new XyPlanPositionCode();
            planPositionCode.setPositionId(positionId);
            planPositionCode.setCodeId(codeId);
            mapper.insert(planPositionCode);
            //生成代码位
            this.addPlanCode(positionInfo);
        }
    }

    /**
     * 品牌广告添加代码位
     *
     * @param positionInfo
     * @throws ServiceException
     */
    public void addPlanCode(XyAdPosition positionInfo) throws ServiceException {

        Long positionId = positionInfo.getId();
        String sourcePlatKey = XyConstant.XUANYUAN_BRAND_SOURCE_PLAT_KEY;
        String sourceCodeId = this.getPlanCodeId(positionId, positionInfo.getAdType());

        XyAdCode adCodeInfo = xyAdCodeService.getDataByPlatKeyAndCodeId(sourcePlatKey, sourceCodeId, null);
        if (adCodeInfo == null) {
            AdCodeRequest adCodeRequest = new AdCodeRequest();
            adCodeRequest.setCodeName(XyConstant.XUANYUAN_BRAND_SOURCE_NAME);
            adCodeRequest.setPositionId(positionInfo.getId());
            adCodeRequest.setUserId(positionInfo.getUserId());
            adCodeRequest.setSourcePlatKey(XyConstant.XUANYUAN_BRAND_SOURCE_PLAT_KEY);
            adCodeRequest.setSourcePlatAccountId(0l);
            adCodeRequest.setSourceCodeId(sourceCodeId);
            adCodeRequest.setRenderType("self");
            adCodeRequest.setIsLadder(0);
            adCodeRequest.setLadderPrice(0L);
            adCodeRequest.setShowLimitHour(null);
            adCodeRequest.setShowLimitDay(null);
            adCodeRequest.setShowIntervalSec(null);
            adCodeRequest.setVersionOperation("-1");
            adCodeRequest.setVersions("");
            adCodeRequest.setChannelOperation("-1");
            adCodeRequest.setChannels("");
            adCodeRequest.setStatus(0);
            adCodeRequest.setNote("");
            adCodeRequest.setIsDel(XyConstant.UN_DEL_STATUS);
            xyAdCodeService.insert(adCodeRequest);

            //添加到默认分组
            XyStrategy defaultStrategyxy = xyStrategyService.getDefaultStrategyByPositionId(positionId);
            XyStrategyCode xyStrategyCode = new XyStrategyCode();
            xyStrategyCode.setStrategyId(defaultStrategyxy.getId());
            xyStrategyCode.setAdCodeId(adCodeRequest.getId());
            xyStrategyCode.setCodeId(sourceCodeId);
            xyStrategyCode.setPriority(0l);
            xyStrategyCode.setRateNum(0l);
            xyStrategyCode.setOrderNum(0l);
            xyStrategyCode.setStatus(1);
            xyStrategyCodeService.putStrategyCode(xyStrategyCode);


        }
    }


    /**
     * 获取广告计划的代码位id
     *
     * @param positionId
     * @param adType
     * @return
     */
    public String getPlanCodeId(Long positionId, String adType) throws ServiceException {
        if (!XyConstant.PLAN_AD_TYPE_CODE_PREFIX_MAP.containsKey(adType)) {
            throw new ServiceException("adType值错误");
        }
        String codeIdPrefix = XyConstant.PLAN_AD_TYPE_CODE_PREFIX_MAP.get(adType);
        String codeId = XyUtil.strPad(String.valueOf(positionId), 10, "0");
        codeId = codeIdPrefix + codeId;
        return codeId;
    }
}
