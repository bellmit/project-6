package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.mapper.ClDeviceMapper;
import com.miguan.ballvideo.service.ClDeviceService;
import com.miguan.ballvideo.vo.ClDeviceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("ClDeviceService")
public class ClDeviceServiceImpl implements ClDeviceService {

    @Resource
    private ClDeviceMapper clDeviceMapper;

    /**
     * 设备开启app
     *
     * @param request
     * @param clDeviceVo 用设备实体
     * @return
     */
    @Override
    public boolean startup(HttpServletRequest request, ClDeviceVo clDeviceVo) {
        String deviceId = clDeviceVo.getDeviceId();
        String appPackage = clDeviceVo.getAppPackage();
        if(clDeviceVo == null){
            return false;
        }
        if(StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(appPackage)) {
            return false;
        }
        clDeviceVo.setUtdId(request.getHeader("utdId"));
        if(StringUtils.isEmpty(clDeviceVo.getDeviceToken()) && StringUtils.isEmpty(clDeviceVo.getHuaweiToken()) && StringUtils.isEmpty(clDeviceVo.getOppoToken())
        && StringUtils.isEmpty(clDeviceVo.getVivoToken()) && StringUtils.isEmpty(clDeviceVo.getXiaomiToken()) ){
            return false;
        }
        ClDeviceVo clDeviceVoExist = getDeviceByDeviceIdAppPackage(deviceId,appPackage);
        // 如果传了多个值，只记录第一个，一个设备应该只传以下5种中的一个 token
        ClDeviceVo clDeviceVoNew = assembleFilterToken(clDeviceVo);
        if (clDeviceVoExist == null) {
            //设备id不存在，新增
            if (clDeviceVoNew != null) {
                try {
                    clDeviceMapper.saveClDevice(clDeviceVoNew);
                } catch (Exception e) {
                    log.error("{}，保存设备Token信息错误!" , deviceId+"|"+appPackage);
                }
            }
        } else {
            //更新 token 和 appPackage
            if (clDeviceVoNew != null) {
                clDeviceMapper.updateClDevice(clDeviceVoNew);
            }
        }
        return true;
    }

    /** 一个设备应该只传以下5种中的一个 token
    * @param clDeviceVo
    * @return ClDeviceVo
    * */
    public ClDeviceVo assembleFilterToken(ClDeviceVo clDeviceVo){
        ClDeviceVo result = new ClDeviceVo();
        result.setId(clDeviceVo.getId());
        result.setDeviceId(clDeviceVo.getDeviceId());
        result.setAppPackage(clDeviceVo.getAppPackage());
        result.setAppVersion(clDeviceVo.getAppVersion());
        if (StringUtils.isNotEmpty(clDeviceVo.getUtdId())) {
            result.setUtdId(clDeviceVo.getUtdId());
        }
        result.setState("10");
        boolean flag = false;
        if(clDeviceVo.getDeviceToken() != null && clDeviceVo.getDeviceToken().length() > 1){
            result.setDeviceToken(clDeviceVo.getDeviceToken());
            flag = true;
        }
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
        if (flag) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public ClDeviceVo getDeviceByDeviceIdAppPackage(String deviceId,String appPackage) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId",deviceId);
        params.put("appPackage",appPackage);
        return clDeviceMapper.getDeviceByDeviceIdAppPackage(params);
    }

    @Override
    public List<ClDeviceVo> findAllTokens(Map<String, Object> params) {
        return clDeviceMapper.findAllTokens(params);
    }

    @Override
    public Integer getAllTokensCountByDistinct(Map<String, Object> params) {
        return clDeviceMapper.getAllTokensCountByDistinct(params);
    }

    @Override
    public Integer getAllTokensCount(String appPackage) {
        return clDeviceMapper.getAllTokensCount(appPackage);
    }

    @Override
    public List<ClDeviceVo> findAllTokensByDistinct(Map<String, Object> params) {
        return clDeviceMapper.findAllTokensByDistinct(params);
    }
}