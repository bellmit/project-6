package com.miguan.xuanyuan.service.impl;

import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.dto.XyStrategyCodeDto;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.dto.request.SourceAppRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyPlat;
import com.miguan.xuanyuan.entity.XyPlatAccount;
import com.miguan.xuanyuan.entity.XyPlatApp;
import com.miguan.xuanyuan.mapper.XyAdCodeMapper;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.vo.AdCodeVo;
import com.miguan.xuanyuan.vo.StrategyCodeVo;
import com.miguan.xuanyuan.vo.StrategyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XyAdCodeServiceImpl implements XyAdCodeService {

    @Resource
    public XyAdCodeMapper xyAdCodeMapper;

    @Resource
    XyAdPositionService xyAdPositionService;

    @Resource
    XyStrategyCodeService xyStrategyCodeService;

    @Resource
    XyPlatAccountService xyPlatAccountService;

    @Resource
    XyPlatService xyPlatService;

    @Resource
    XySourceAppService xySourceAppService;



    public long insert(AdCodeRequest adCodeRequest) throws ServiceException {
        xyAdCodeMapper.insert(adCodeRequest);
        return adCodeRequest.getId();
    }

    public int update(AdCodeRequest adCodeRequest) throws ServiceException {
        return xyAdCodeMapper.update(adCodeRequest);
    }


    public AdCodeVo findById(Long id) {
        XyAdCode xyAdCode = xyAdCodeMapper.findById(id);
        if (xyAdCode == null) {
            return null;
        }
        String sourceApp = xySourceAppService.findSourceAppByPositionIdAndPlatKey(xyAdCode.getPositionId(),xyAdCode.getSourcePlatKey());
        xyAdCode.setSourceAppId(sourceApp);
        AdCodeVo adCodeVo = new AdCodeVo();
        BeanUtils.copyProperties(xyAdCode, adCodeVo);
        return adCodeVo;
    }

    @Transactional
    public StrategyCodeVo addCode(AdCodeRequest adCodeRequest) throws ServiceException {

        AdPositionDetailDto adPositionDetailDto = xyAdPositionService.getPositionDetail(adCodeRequest.getPositionId());
        if (adPositionDetailDto == null) {
            throw new ServiceException("广告位不存在");
        }

        String sourcePlatKey = adCodeRequest.getSourcePlatKey();
        XyPlat platInfo = xyPlatService.getPlatDataByPlatKey(sourcePlatKey);
        if (platInfo == null) {
            throw new ServiceException("广告平台参数（sourcePlatKey）错误");
        }

        String sourceCodeId = adCodeRequest.getSourceCodeId();
        Long adCodeId = adCodeRequest.getId();
        XyAdCode adCodeInfo = getDataByPlatKeyAndCodeId(sourcePlatKey, sourceCodeId, adCodeId);
        if (adCodeInfo != null) {
            throw new ServiceException("代码位ID（sourceCodeId）不能重复");
        }

        Long sourcePlatAccountId = adCodeRequest.getSourcePlatAccountId();
        if (sourcePlatAccountId == null) {
            sourcePlatAccountId = 0l;
        }
//        Long userId = adCodeRequest.getUserId();
//        List<XyPlatAccount>  userPatAccountList = xyPlatAccountService.getUserPlatAccountList(userId);
//
//        if (CollectionUtils.isEmpty(userPatAccountList)) {
//            throw new ServiceException("当前用户没有对应平台账号");
//        }
//
//        Map<Long, XyPlatAccount> userPatAccountMap = new HashMap<>();
//        userPatAccountMap = userPatAccountList.stream().collect(Collectors.toMap(XyPlatAccount::getId, xyPlatAccount -> xyPlatAccount));
//
//        if (sourcePlatAccountId == null) {
//            if (userPatAccountList.size() == 1) {
//                sourcePlatAccountId = userPatAccountList.get(0).getId();
//                adCodeRequest.setSourcePlatAccountId(userPatAccountList.get(0).getId());
//            } else {
//                throw new ServiceException("请选择平台账号");
//            }
//        } else {
//            if (!userPatAccountMap.containsKey(sourcePlatAccountId)) {
//                throw new ServiceException("平台账号sourcePlatAccountId错误");
//            }
//        }

        //设置app与第三方平台app关联
        Long appId = adPositionDetailDto.getAppId();
        Long platId = platInfo.getId();
        String sourceAppId = adCodeRequest.getSourceAppId();
        xySourceAppService.putSourceApp(appId, platId, sourceAppId) ;

        adCodeRequest.setSourcePlatAccountId(sourcePlatAccountId);
        Long id = this.insert(adCodeRequest);

        AdCodeVo adCodeVo = findById(id);
        StrategyCodeVo vo = new StrategyCodeVo();
        vo.setStrategyCodeId(0L);
        vo.setCodeId(id);
        vo.setSourceCodeId(adCodeVo.getSourceCodeId());
        vo.setCodeName(adCodeVo.getCodeName());
        vo.setIsLadder(adCodeVo.getIsLadder());
        vo.setLadderPrice(adCodeVo.getLadderPrice());
        vo.setPriority(0L);
        vo.setRateNum(0L);
        vo.setCodeStatus(0);
        vo.setStatus(0);
        vo.setEcpm(0);
        vo.setProfitCnt(0);
        vo.setShowCnt(0);
        vo.setClickCnt(0);
        return  vo;
    }

    public StrategyCodeVo editCode(AdCodeRequest adCodeRequest) throws ServiceException {
        AdPositionDetailDto adPositionDetailDto = xyAdPositionService.getPositionDetail(adCodeRequest.getPositionId());
        if (adPositionDetailDto == null) {
            throw new ServiceException("广告位不存在");
        }

        String sourcePlatKey = adCodeRequest.getSourcePlatKey();
        String sourceCodeId = adCodeRequest.getSourceCodeId();
        Long id = adCodeRequest.getId();
        XyAdCode adCodeInfo = getDataByPlatKeyAndCodeId(sourcePlatKey, sourceCodeId, id);
        if (adCodeInfo != null) {
            throw new ServiceException("代码位ID（sourceCodeId）不能重复");
        }
        XyPlat platInfo = xyPlatService.getPlatDataByPlatKey(sourcePlatKey);
        if (platInfo == null) {
            throw new ServiceException("广告平台参数（sourcePlatKey）错误");
        }

        Long userId = adCodeRequest.getUserId();
        if (adPositionDetailDto.getUserId() != userId) {
            throw new ServiceException("不能修改非自己的广告位");
        }

        Long sourcePlatAccountId = adCodeRequest.getSourcePlatAccountId();
//        List<XyPlatAccount>  userPatAccountList = xyPlatAccountService.getUserPlatAccountList(userId);
//        if (CollectionUtils.isEmpty(userPatAccountList)) {
//            throw new ServiceException("当前用户没有对应平台账号");
//        }
//        Map<Long, XyPlatAccount> userPatAccountMap = new HashMap<>();
//        userPatAccountMap = userPatAccountList.stream().collect(Collectors.toMap(XyPlatAccount::getId, xyPlatAccount -> xyPlatAccount));
//
//        if (sourcePlatAccountId == null) {
//            if (userPatAccountList.size() == 1) {
//                sourcePlatAccountId = userPatAccountList.get(0).getId();
//                adCodeRequest.setSourcePlatAccountId(userPatAccountList.get(0).getId());
//            } else {
//                throw new ServiceException("请选择平台账号");
//            }
//        } else {
//            if (!userPatAccountMap.containsKey(sourcePlatAccountId)) {
//                throw new ServiceException("平台账号sourcePlatAccountId错误");
//            }
//        }

        adCodeRequest.setSourcePlatAccountId(sourcePlatAccountId);

        //create by kkh
        //设置app与第三方平台app关联
        Long appId = adCodeRequest.getAppId();
        Long platId = platInfo.getId();
        String sourceAppId = adCodeRequest.getSourceAppId();
        xySourceAppService.putSourceApp(appId, platId, sourceAppId) ;

        this.update(adCodeRequest);
        XyStrategyCodeDto xyStrategyCodeDto = xyStrategyCodeService.getStrategyCodeInfo(adCodeRequest.getId());
        StrategyCodeVo strategyCodeVo = new StrategyCodeVo();
        BeanUtils.copyProperties(xyStrategyCodeDto, strategyCodeVo);
        return  strategyCodeVo;
    }


    /**
     * 获取平台和代码位信息
     *
     * @param sourcePlatKey
     * @param sourceCodeId
     * @param id
     * @return
     */
    public XyAdCode getDataByPlatKeyAndCodeId(String sourcePlatKey, String sourceCodeId, Long id) {
        return xyAdCodeMapper.getDataByPlatKeyAndCodeId(sourcePlatKey, sourceCodeId, id);
    }

    public Long getCodePutInStatus(String codeId) {
        return xyAdCodeMapper.getCodePutInStatus(codeId);
    }

    public int updateCodeNotPutIn(Long positionId) {
        return xyAdCodeMapper.updateCodeNotPutIn(positionId);
    }

    @Override
    public void deleteByPositionId(Long positionId) {
        xyAdCodeMapper.deleteByPositionId(positionId);
    }

    @Override
    public void deleteByAppId(Long appId) {
        xyAdCodeMapper.deleteByAppId(appId);
    }

    @Override
    public String findSourceAppId(Long appId, String platKey) {
        return xySourceAppService.findSourceAppId(appId, platKey);
    }

    @Override
    public List<XyAdCode> findByAppIdAndPlatKey(Long appId, String platKey) {
        return xyAdCodeMapper.findByAppIdAndPlatKey(appId,platKey);
    }
}
