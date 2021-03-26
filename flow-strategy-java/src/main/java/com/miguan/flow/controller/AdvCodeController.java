package com.miguan.flow.controller;

import com.google.common.collect.Lists;
import com.miguan.flow.common.aop.AbTestAdvParams;
import com.miguan.flow.common.util.ChannelUtil;
import com.miguan.flow.common.util.FlowUtil;
import com.miguan.flow.common.util.PackageUtil;
import com.miguan.flow.common.util.VersionUtil;
import com.miguan.flow.dto.AdvertCodeDto;
import com.miguan.flow.dto.AdvertCodeParamDto;
import com.miguan.flow.dto.common.AbTestAdvParamsDto;
import com.miguan.flow.service.AdvCodeService;
import com.miguan.flow.vo.common.PublicInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miguan.flow.common.util.FlowUtil.removeEndNum;

@Api(value = "广告代码位接口", tags = {"广告代码位接口"})
@RestController
@Slf4j
@RequestMapping("/api/advertCode")
public class AdvCodeController {

    @Resource
    private AdvCodeService advCodeService;

    @ApiOperation(value = "广告代码位信息列表接口(视频3.2.23版本及之后版本)", consumes = "none")
    @PostMapping("/infoList")
    public List<AdvertCodeDto> advCodeInfoList(@AbTestAdvParams AbTestAdvParamsDto queueVo,
                                               @RequestHeader(value = "Public-Info", required = false) String publicInfoStr,
                                               AdvertCodeParamDto paramDto) {
        if (paramDto == null || StringUtils.isBlank(paramDto.getPositionType()) || StringUtils.isBlank(paramDto.getAppPackage())
                || StringUtils.isBlank(paramDto.getAppVersion()) || StringUtils.isBlank(paramDto.getMobileType())) {
            return Lists.newArrayList();
        }
        log.debug("infoList:上报的publicInfo:{}", publicInfoStr);
        log.debug("infoList:AbTestAdvParamsDto:{}", queueVo);

        PublicInfo publicInfo = new PublicInfo();
        publicInfo.setNewApp(paramDto.getIsNewApp() != null && paramDto.getIsNewApp() == 1);  //是否新老用户
        String parentChannel = removeEndNum(paramDto.getChannelId());
        publicInfo.setChannel(parentChannel);  //父渠道
        paramDto.setChannelId(parentChannel);
        publicInfo.setGpscity(paramDto.getGpscity());  //城市
        if(StringUtil.isNotBlank(publicInfoStr)) {
            publicInfo = new PublicInfo(publicInfoStr);
        }
        List<AdvertCodeDto> advertCodes = advCodeService.advCodeInfoList(queueVo, publicInfo, paramDto);
        return advertCodes;
    }

    @ApiOperation("统计多维度代码位ecpm")
    @PostMapping("/countMultiEcpm")
    public Map<String, Double> countMultiEcpm(@ApiParam("代码位id，多个逗号分隔") @RequestParam String adIds,
                                              @ApiParam("是否新用户，1：新用户，0：老用户") @RequestParam(defaultValue = "0") Integer isNew,
                                              @ApiParam("城市，例如：厦门市") String city,
                                              @ApiParam("渠道") String channel,
                                              @ApiParam("包名") String appPackage) {
        List<String> adIdList = Arrays.asList(adIds.split(","));
        Map<String, Object> params = new HashMap<>();
        params.put("isNew", isNew);
        params.put("city", city);
        params.put("channel", channel);
        params.put("appPackage", appPackage);
        return advCodeService.countMultiEcpm(adIdList, params);
    }
}
