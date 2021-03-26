package com.miguan.ballvideo.controller;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.common.util.dsp.DspConstant;
import com.miguan.ballvideo.entity.dsp.AdvZoneValExpVo;
import com.miguan.ballvideo.entity.dsp.AdvertDspInfo;
import com.miguan.ballvideo.service.dsp.AdvertDspService;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.head.PublicInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suhongju
 */

@Slf4j
@Api(value="Dsp自投平台Controller",tags={"Dsp App接口"})
@RestController
@RequestMapping("/api/advDsp")
public class AdvertDspController {

    @Resource
    private AdvertDspService advertDspService;

    @ApiOperation("广告信息代码位列表")
    @PostMapping("/getAdvCode")
    public ResultMap<List<AdvertCodeVo>> getAdvCode(@AbTestAdvParams AbTestAdvParamsVo queueVo,
                                                    @ApiParam(value = "应用包名") String appPackage,
                                                    @ApiParam(value = "手机类型：1-ios，2：安卓") String mobileType,
                                                    @ApiParam(value = "校验合法性签名") String secretkey,
                                                    @ApiParam(value = "渠道ID")String channelId,
                                                    @ApiParam(value = "app的版本") String appVersion,
                                                    @ApiParam(value = "本次请求广告位信息") String poskey) {
        try {
            if(StringUtil.isEmpty(appPackage)){
                return ResultMap.error("缺少必要的应用包名");
            }
            if(StringUtil.isEmpty(secretkey)){
                return ResultMap.error("缺少必要的验证码");
            }
            if(StringUtil.isEmpty(poskey)){
                return ResultMap.error("缺少必要的广告位信息");
            }
            if(StringUtil.isEmpty(mobileType)){
                return ResultMap.error("缺少必要的手机类型");
            }
            //获取appId的MD5值前16位
            String md5AppId16 = DigestUtils.md5DigestAsHex(appPackage.getBytes()).substring(0,16);
            String secKey = null;
            try {
                secKey = AESUtils.decrypt(secretkey, md5AppId16);
                if(StringUtil.isEmpty(secKey)){
                    return ResultMap.error("非法的验证码");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultMap.error("非法的验证码");
            }
            Map<String, Object> param = new HashMap<>();
            param.put("appPackage", appPackage);
            param.put("secretkey", secKey);
            param.put("poskey", poskey);
            param.put("appVersion", appVersion);
            param.put("channelId", channelId);
            Map<String,Integer> queryMap = advertDspService.judgeValidity(param);
            if(MapUtils.isNotEmpty(queryMap)){
                if(MapUtils.getInteger(queryMap,"appCnt") == 0){
                    return ResultMap.error("请输入有效的应用包名");
                }
                if(MapUtils.getInteger(queryMap,"secKeyCnt") == 0){
                    return ResultMap.error("请输入有效的验证码");
                }
                if(MapUtils.getInteger(queryMap,"posKeyCnt") == 0){
                    return ResultMap.error("请输入有效的广告位信息");
                }
            }
            param.put("mobileType", mobileType);
            List<AdvertCodeVo> adList = advertDspService.commonSearch(queueVo, param);
            if(adList==null){
                return ResultMap.success(Lists.newArrayList());
            }
            //CPM竞价
//            advertDspService.cpmAdvCode(adList);
            return ResultMap.success(adList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }




    @ApiOperation("获取广告内容")
    @PostMapping("/getAdvInfo")
    public ResultMap<AdvertDspInfo> getAdvInfo(HttpServletRequest request,
                                               @ApiParam(value = "应用包名") String appPackage,
                                               @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                               @ApiParam(value = "代码位ID") String code,
                                               @ApiParam(value = "设备号") String device,
                                               @ApiParam(value = "应用版本号") String appVersion
    ) {
        if(StringUtil.isEmpty(appPackage)){
            return ResultMap.error("缺少必要的应用包名");
        }
        if(StringUtil.isEmpty(mobileType)){
            return ResultMap.error("缺少必要的手机类型");
        }
        if(StringUtil.isEmpty(code)){
            return ResultMap.error("缺少必要的代码位ID");
        }
        if(StringUtil.isEmpty(device)){
            return ResultMap.error("缺少必要的设备号");
        }
        if(StringUtil.isEmpty(appVersion)){
            return ResultMap.error("缺少必要的应用版本号");
        }
        String commonAttr = request.getHeader("Common-Attr");
        String uuid = null;
        if(StringUtil.isNotBlank(commonAttr)) {
            byte[] presetPropByte = Base64Utils.decodeFromString(commonAttr);
            log.info("解密前的commonAttr:{}", commonAttr);
            commonAttr = new String(presetPropByte, StandardCharsets.UTF_8);
            String[] attr = commonAttr.split("::");
            log.info("解密后的commonAttr:{}", commonAttr);
            uuid = attr[2];
        }

        PublicInfo publicInfo = new PublicInfo(request.getHeader("Public-Info"));
        String area = publicInfo.getGpscity();  //城市
        String devTp = publicInfo.getManufacturer();  //手机品牌
        log.info("获取城市和手机品牌:{},{}", area, devTp);

        try {
            //判断是否为98自投平台的代码位,不是则为第三方
            String appId = advertDspService.judgeAdvPlat(code, appPackage, mobileType);
            if(StringUtil.isEmpty(appId))
                return ResultMap.error(201,"通过SDK发起请求，请求第三方广告");
            //返回98自投平台的广告内容
            AdvertDspInfo resultMap = advertDspService.getAdvInfo(appId,code,area,devTp,device,uuid,appVersion);

            return ResultMap.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }

    }


    @ApiOperation("上报广告事件")
    @PostMapping("/upReport")
    public ResultMap upReport(HttpServletRequest request,
                              @ApiParam("广告有效点击/曝光实体") @ModelAttribute AdvZoneValExpVo advZoneValExpVo,
                              @RequestHeader(value = "Public-Info") String publicInfo,
                              @ApiParam(value = "订单ID") String orderId,
                              @ApiParam(value = "设备号") String device,
                              @ApiParam(value = "应用Id") String appId) {
        //String publicInfo = "bnVsbDo6bnVsbDo6bnVsbDo6bnVsbDo6bnVsbDo6QW5kcm9pZDo6MTA6OjIxMzk6OjEwODA6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6Om51bGw6OjEuMDo6bnVsbDo6bnVsbA";
        log.info("上报的publicInfo:{}", publicInfo);
        if(StringUtil.isEmpty(orderId)){
            return ResultMap.error("缺少必要的订单ID");
        }
        if(StringUtil.isEmpty(device)){
            return ResultMap.error("缺少必要的设备号");
        }
        if(StringUtil.isEmpty(appId)){
            return ResultMap.error("缺少必要的应用Id");
        }
        if(StringUtil.isEmpty(advZoneValExpVo.getDesign_id())){
            return ResultMap.error("缺少必要的创意ID");
        }
        if(StringUtil.isEmpty(advZoneValExpVo.getPlan_id())){
            return ResultMap.error("缺少必要的计划ID");
        }
        if(StringUtils.isBlank(advZoneValExpVo.getPrice())){
            return ResultMap.error("缺少必要的出价");
        }
        String advertOrderEffectMongodb = null;
        if(DspConstant.ADVERT_ZONE_VALID_CLICK.equals(advZoneValExpVo.getAction_id())){
            advertOrderEffectMongodb = Constant.ADVERT_ORDER_EFFECT_MONGODB;
        }else if(DspConstant.ADVERT_ZONE_EXPOSURE.equals(advZoneValExpVo.getAction_id())){
            advertOrderEffectMongodb = Constant.ADVERT_ORDER_EXPOSURE_MONGODB;
        }
        if(advertOrderEffectMongodb == null){
            return ResultMap.error("actionId传入出错，actionId="+advZoneValExpVo.getAction_id());
        }
        try {
            boolean exsist = advertDspService.judgeOrderExsist(appId, orderId, advertOrderEffectMongodb);
            if(exsist){
                return ResultMap.success(null,"订单ID已经存在,orderId="+orderId);
            }
            if(DspConstant.ADVERT_ZONE_VALID_CLICK.equals(advZoneValExpVo.getAction_id())){
                boolean upFlag = advertDspService.upOrderReport(advZoneValExpVo.getPlan_id());
                if(upFlag){
                    advertDspService.saveOrderAndSendToMQ(advZoneValExpVo,publicInfo, request, orderId, device, appId, advertOrderEffectMongodb);
                }
            }else if(DspConstant.ADVERT_ZONE_EXPOSURE.equals(advZoneValExpVo.getAction_id())){
                advertDspService.saveOrderAndSendToMQ(advZoneValExpVo,publicInfo, request, orderId, device, appId, advertOrderEffectMongodb);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
        return ResultMap.success();
    }

    @ApiOperation("重载配置参数")
    @PostMapping("/reloadConf")
    public ResultMap reloadConf(){
        try {
            advertDspService.initDspConfig();
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
