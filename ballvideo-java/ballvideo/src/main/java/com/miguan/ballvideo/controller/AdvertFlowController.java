package com.miguan.ballvideo.controller;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.AdvertFlowService;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.video.AdvertFlowCodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suhongju
 */

@Slf4j
@Api(value="流量变现平台Controller",tags={"流量变现平台接口"})
@RestController
@RequestMapping("/api/advFlowCode")
public class AdvertFlowController {

    @Resource
    private AdvertFlowService advertFlowService;

    @ApiOperation("广告信息列表接口V1.0")
    @PostMapping("/getAdvInfo")
    public ResultMap<List<AdvertFlowCodeVo>> getAdvInfoByFlow(@AbTestAdvParams AbTestAdvParamsVo queueVo,
                                                              @ApiParam(value = "应用唯一标识")String appId,
                                                              @ApiParam(value = "校验合法性签名") String secretkey,
                                                              @ApiParam(value = "本次请求广告位信息") String poskey,
                                                              @ApiParam(value = "操作系统。可能取值：ios；android；other；") String os,
                                                              @ApiParam(value = "IMEI[安卓设备]") String imei,
                                                              @ApiParam(value = "OAID[安卓设备]") String oaId,
                                                              @ApiParam(value = "AndroidID[安卓设备]") String androidId,
                                                              @ApiParam(value = "idfa[iOS设备]") String idfa,
                                                              @ApiParam(value = "联网方式。可能取值：0:unknown；1:WiFi；2:2g；3:3g；4:4g；") String conn,
                                                              @ApiParam(value = "运营商。可能取值：0:unknown；1:移动；2:联通；3:电信；") String carrier,
                                                              @ApiParam(value = "gps定位。ddd.ddddd， 度 . 度的十进制小数部分（5位）") String location
                                                          ) {
        if(StringUtils.isBlank(appId)){
            return ResultMap.error(401,"缺少必要的appId");
        }
        if(StringUtils.isBlank(secretkey)){
            return ResultMap.error(401,"缺少必要的验证码");
        }
        if(StringUtils.isBlank(poskey)){
            return ResultMap.error(401,"缺少必要的广告位信息");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("appId", appId);
        param.put("secretkey", secretkey);
        param.put("poskey", poskey);
        Map<String,Integer> queryMap = advertFlowService.judgeValidity(param);
        if(MapUtils.isNotEmpty(queryMap)){
            if(MapUtils.getInteger(queryMap,"appCnt") == 0){
                return ResultMap.error(402,"请输入有效的appId");
            }
            if(MapUtils.getInteger(queryMap,"secKeyCnt") == 0){
                return ResultMap.error(402,"请输入有效的验证码");
            }
            if(MapUtils.getInteger(queryMap,"posKeyCnt") == 0){
                return ResultMap.error(402,"请输入有效的广告位信息");
            }
        }
        param.put("queryType", "flow");
        param.put("os", os);
        param.put("imei", imei);
        param.put("oaId", oaId);
        param.put("androidId", androidId);
        param.put("idfa", idfa);
        param.put("conn", conn);
        param.put("carrier", carrier);
        param.put("location", location);
        List<AdvertCodeVo> adList = advertFlowService.commonSearch(queueVo, param);
        if(adList==null){
            return ResultMap.success(Lists.newArrayList());
        }
        return ResultMap.success(bulidFlowList(adList));
    }


    private List<AdvertFlowCodeVo> bulidFlowList(List<AdvertCodeVo> adList){
        List<AdvertFlowCodeVo> flowLst = Lists.newArrayList();
        adList.stream().forEach(ad ->{
            AdvertFlowCodeVo flowCodeVo = new AdvertFlowCodeVo();
            flowCodeVo.setId(ad.getId());
            flowCodeVo.setAdId(ad.getAdId());
            flowCodeVo.setFirstLoadPosition(ad.getFirstLoadPosition());
            flowCodeVo.setSecondLoadPosition(ad.getSecondLoadPosition());
            flowCodeVo.setMaxShowNum(ad.getMaxShowNum());
            flowCodeVo.setPlat(ad.getPlat());
            flowCodeVo.setAdType(ad.getAdType());
            flowCodeVo.setRender(ad.getRender());
            flowCodeVo.setMaterial(ad.getMaterial());
            flowCodeVo.setTitle(ad.getTitle());
            flowCodeVo.setUrl(ad.getUrl());
            flowCodeVo.setLinkType(ad.getLinkType());
            flowCodeVo.setImgPath(ad.getImgPath());
            flowCodeVo.setPermission(ad.getPermission());
            flowCodeVo.setLadderPrice(ad.getLadderPrice());
            flowLst.add(flowCodeVo);
        });
        return flowLst;
    }

}
