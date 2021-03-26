package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.adv.AdvFieldType;
import com.miguan.laidian.common.util.adv.AdvUtils;
import com.miguan.laidian.dynamicquery.Dynamic3Query;
import com.miguan.laidian.service.AdvertService;
import com.miguan.laidian.service.MoFangService;
import com.miguan.laidian.vo.AdvertCodeVo;
import com.miguan.laidian.vo.AdvertPositionListVo;
import com.miguan.laidian.vo.AdvertPositionVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 广告ServiceImpl
 * @author laiyd
 * @date 2020-05-07
 **/

@Service("AdvertService")
public class AdvertServiceImpl implements AdvertService {

    @Resource
    private Dynamic3Query dynamic3Query;

    @Resource
    private MoFangService moFangService;

    /**
     * 查询广告配置
     *
     * @param postitionType 广告位置类型，如果为空，则查询全部
     * @param adPermission  是否有IMEI权限：0否 1是
     * @return
     */
    @Override
    public List<AdvertCodeVo> queryAdertList(CommonParamsVo commomParams, String postitionType, String adPermission) {
        Map<String, Object> param = new HashMap<>();
        param.put("postitionType", postitionType);
        param.put("mobileType", commomParams.getMobileType());
        param.put("channelId", commomParams.getChannelId());
        param.put("appType", commomParams.getAppType());
        param.put("adPermission", adPermission);
        param.put("appVersion", commomParams.getAppVersion());
        List<AdvertCodeVo> advertCodeVos = getAdvertsByParams(param, AdvFieldType.All);
        if(CollectionUtils.isEmpty(advertCodeVos))return null;
        if (advertCodeVos.get(0).getComputer() == 1) {
            return AdvUtils.computerAndSort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 2){
            return AdvUtils.sort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 3){
            //自动排序
            return AdvUtils.sort(advertCodeVos);
        }
        return advertCodeVos;
    }

    @Override
    public AdvertPositionListVo advPositionInfo(CommonParamsVo commomParams) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobileType", commomParams.getMobileType());
        param.put("appType", commomParams.getAppType());
        List<AdvertPositionVo> sysVersionVos = dynamic3Query.getAdversPositionInfo(param,AdvFieldType.PostitionInfo);
        for (AdvertPositionVo advo : sysVersionVos) {
            if (advo.getFirstLoadPosition() == null) {
                advo.setFirstLoadPosition(0);
            }
            if (advo.getSecondLoadPosition() == null) {
                advo.setSecondLoadPosition(0);
            }
            if (advo.getMaxShowNum() == null) {
                advo.setMaxShowNum(0);
            }
        }
        int laidianUnlockDays = Global.getInt("laidian_unlock_days", commomParams.getAppType());
        int laidianForceDays = Global.getInt("laidian_force_days", commomParams.getAppType());
        int callDetailsQuitLimit = Global.getInt("call_details_quit_limit", commomParams.getAppType());
        AdvertPositionListVo advertPositionListVo = new AdvertPositionListVo();
        advertPositionListVo.setAdvertPositionVoList(sysVersionVos);
        advertPositionListVo.setLaidianUnlockDays(laidianUnlockDays);
        advertPositionListVo.setLaidianForceDays(laidianForceDays);
        advertPositionListVo.setCallDetailsQuitLimit(callDetailsQuitLimit);
        return advertPositionListVo;
    }

    public List<AdvertCodeVo> getAdvertsByParams(Map<String, Object> param,int fieldType) {
        //魔方后台-广告总开关:true禁用，false非禁用
        if (moFangService.stoppedByMofang(param)) {
            return null;
        }
        List<AdvertCodeVo> sysVersionVos = dynamic3Query.getAdversWithCache(param,fieldType);
        return sysVersionVos;
    }
}