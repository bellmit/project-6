package com.miguan.laidian.service.impl;

import com.miguan.laidian.mapper.ClDeviceMapper;
import com.miguan.laidian.service.ClDeviceService;
import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.ClUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户表ServiceImpl
 *
 * @author laiyd
 * @date 2020-07-07
 **/
@Slf4j
@Service("ClDeviceService")
public class ClDeviceServiceImpl implements ClDeviceService {

    @Resource
    private ClDeviceMapper clDeviceMapper;

    /**
     * 设备开启app
     * @param clDeviceVo 用设备实体
     * @return
     */
    @Override
    public boolean startup(ClDeviceVo clDeviceVo) {
        ClDeviceVo clDeviceVoExist = getDeviceByDeviceIdAppPackage(clDeviceVo.getDeviceId(),clDeviceVo.getAppType());
        // 如果传了多个值，只记录第一个，一个设备应该只传以下5种中的一个 token
        ClDeviceVo clDeviceVoNew = assembleFilterToken(clDeviceVo);
        if (clDeviceVoExist == null) {
            //设备id不存在，新增
            clDeviceMapper.saveClDevice(clDeviceVoNew);
        } else {
            //更新 token 和 appType
            clDeviceMapper.updateClDevice(clDeviceVoNew);
        }
        return true;
    }

    /** 一个设备应该只传以下5种中的一个 token
    * @param clDeviceVo
    * @return ClDeviceVo
    * */
    public ClDeviceVo assembleFilterToken(ClDeviceVo clDeviceVo){
        ClDeviceVo result = new ClDeviceVo();
        BeanUtils.copyProperties(clDeviceVo, result);
        result.setState("10");
        if(clDeviceVo.getHuaweiToken() != null && clDeviceVo.getHuaweiToken().length() > 1){
            result.setHuaweiToken(clDeviceVo.getHuaweiToken());
            return result;
        }
        if(clDeviceVo.getOppoToken() != null && clDeviceVo.getOppoToken().length() > 1){
            result.setOppoToken(clDeviceVo.getOppoToken());
            return result;
        }
        if(clDeviceVo.getVivoToken() != null && clDeviceVo.getVivoToken().length() > 1){
            result.setVivoToken(clDeviceVo.getVivoToken());
            return result;
        }
        if(clDeviceVo.getXiaomiToken() != null && clDeviceVo.getXiaomiToken().length() > 1){
            result.setXiaomiToken(clDeviceVo.getXiaomiToken());
            return result;
        }
        return null;
    }

    public ClDeviceVo getDeviceByDeviceIdAppPackage(String deviceId, String appType) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId",deviceId);
        params.put("appType",appType);
        return clDeviceMapper.getDeviceByDeviceIdAppType(params);
    }

    @Override
    public List<ClUserVo> findAllTokens(Map<String, Object> params) {
        return clDeviceMapper.findAllTokens(params);
    }

    @Override
    public int countClDevice(Map<String, Object> params) {
        return clDeviceMapper.countClDevice(params);
    }

    @Override
    public List<ClDeviceVo> findAllTokensByDistinct(Map<String, Object> params) {
        return clDeviceMapper.findAllTokensByDistinct(params);
    }
}