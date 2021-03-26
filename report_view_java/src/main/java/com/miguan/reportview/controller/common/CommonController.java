package com.miguan.reportview.controller.common;

import com.google.common.collect.Maps;
import com.miguan.reportview.common.enmus.AdcTypeEnmu;
import com.miguan.reportview.common.enmus.RenderTypeEnmu;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.entity.AdPlat;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.entity.DwAppVersionChannelDict;
import com.miguan.reportview.entity.VideosCat;
import com.miguan.reportview.service.*;
import com.miguan.reportview.vo.AdSpaceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhongli
 * @date 2020-08-03
 *
 */
@Api(value = "公共接口", tags = {"公共接口"})
@RestController
@Slf4j
public class CommonController extends BaseController {
    @Autowired
    private IAppService appService;
    @Autowired
    private IAppVersionSetService appVersionSetService;
    @Autowired
    private IAdAdvertService adAdvertService;
    @Autowired
    private IAdPlatService adPlatService;
    @Autowired
    private IVideosService videosService;
    @Autowired
    private ILdService ldService;
    @Autowired
    private ICickhouseCommonService cickhouseCommonService;

    @PostMapping("api/init")
    public void initApp() {
        appService.sysnApp();
        adAdvertService.sysAdSpace();
    }

    @ApiOperation(value = "获取app、版本号和平台")
    @PostMapping("api/common/get/appAndVersionAndPlat")
    public ResponseEntity<Map<String, List>> getAppAndVersionAndPlat(
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        List<App> apps = appService.getApps();
        filter(apps, appType);
        List<String> versions = appVersionSetService.getAppVersion(appType);
        List<AdPlat> plats = adPlatService.getAllPlat();
        Map<String, List> map = Maps.newHashMapWithExpectedSize(3);
        map.put("app", apps);
        map.put("version", versions);
        map.put("plat", plats);
        return success(map);
    }

    private void filter(List<App> apps, int appType) {
        Iterator<App> iterator = apps.iterator();
        while(iterator.hasNext()) {
            App app = iterator.next();
            if(appType == 1 && "com.mg.phonecall".equals(app.getAppPackage())) {
                iterator.remove();
            } else if(appType == 2 && !"com.mg.phonecall".equals(app.getAppPackage())) {
                iterator.remove();
            }
        }
    }


    @ApiOperation(value = "根据应用获取广告位")
    @PostMapping("api/common/get/adspace")
    public ResponseEntity<List<AdSpaceVo>> getAdSpace(@ApiParam(value = "应用马甲包 多个用,隔开，为空表示获取所有广告位")
                                                      @RequestParam(required = false) String appPackage,
                                                      @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        String[] appPackages = null;
        if (StringUtils.isNotBlank(appPackage)) {
            appPackages = appPackage.split(",");
            appPackages = Stream.of(appPackages).filter(Objects::nonNull).toArray(String[]::new);
        }
        List<AdSpaceVo> list = adAdvertService.getAdSpaceByApp(appType, appPackages);
        return success(list);
    }

    @ApiOperation(value = "模糊查询代码位ID")
    @PostMapping("api/common/find/adcode")
    public ResponseEntity<List<String>> findAdCode(@ApiParam(value = "广告位ID前缀") String code) {
        List<String> list = adAdvertService.findAdCodeForLike(code);
        return success(list);
    }

    @ApiOperation(value = "获取视频或来电分类")
    @PostMapping("api/common/get/videoscat")
    public ResponseEntity<List<VideosCat>> getVideoscat(
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        List<VideosCat> list = new ArrayList<>();
        if(appType == 1) {
            //视频分类
            list = videosService.getVideosCat();
        } else if(appType == 2) {
            //来电分类
            list = ldService.getVideosCat();
            list = list.stream().filter(r -> "1".equals(r.getType())).collect(Collectors.toList());
        }
        return success(list);
    }


    @ApiOperation(value = "获取父渠道")
    @PostMapping("api/common/get/parentChannelDist")
    public ResponseEntity<List<Object>> getParentChannelDist(
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        List<Object> list = cickhouseCommonService.getParentChannelDist(appType);
        return success(list);
    }

    @ApiOperation(value = "获取子渠道")
    @PostMapping("api/common/get/subChannelDist")
    public ResponseEntity<List<DwAppVersionChannelDict>> getSubChannelDist(@ApiParam(value = "父渠道") String parent,
                                                                           @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        if (StringUtils.isBlank(parent)) {
            throw new NullParameterException();
        }
        List<DwAppVersionChannelDict> list = cickhouseCommonService.getSubChannelDist(parent, appType);
        return success(list);
    }

    @ApiOperation(value = "获取所有子渠道")
    @PostMapping("api/common/get/allSubChannel")
    public ResponseEntity<List<DwAppVersionChannelDict>> getAllSubChannelDist(@ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType) {
        List<DwAppVersionChannelDict> list = cickhouseCommonService.getAllSubChannel(appType);
        return success(list);
    }

    @ApiOperation(value = "获取机型")
    @PostMapping("api/common/get/model")
    public ResponseEntity<List<Object>> getModel() {
        List<Object> list = cickhouseCommonService.getModel();
        return success(list);
    }

    @ApiOperation(value = "获取渲染方式和素材")
    @PostMapping("api/common/get/getRenderTypeAndAdcType")
    public ResponseEntity getRenderTypeAndAdcType() {
        Map<String, Object> map = new HashMap<>();
        map.put("adcType", AdcTypeEnmu.getList());
        map.put("renderType", RenderTypeEnmu.getList());
        return success(map);
    }
}
