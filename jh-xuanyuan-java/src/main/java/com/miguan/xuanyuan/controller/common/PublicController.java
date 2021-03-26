package com.miguan.xuanyuan.controller.common;

import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.constant.OptionConfigConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.OperationEnum;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.dto.ab.AbLayer;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.vo.VersionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author kangkunhuang
 * @Description 公共类
 * @Date 2021/1/21
 **/
@Api(value = "公共类控制层", tags = {"公共类接口"})
@RestController
@Slf4j
@RequestMapping("/api/common/public")
public class PublicController {

    @Resource
    private XyOptionItemConfigService xyOptionItemConfigService;

    @Resource
    private XyPlatService xyPlatService;
    @Resource
    private XyRenderService xyRenderService;

    @Resource
    private MofangService mofangService;

    @Resource
    private UserService userService;

    @Resource
    private XyAdShapeService xyAdShapeService;

    @Resource
    AbExpService abExpService;

    @ApiOperation("查询配置项列表")
    @GetMapping("/findItem")
    public ResultMap<List<XyOptionItemConfig>> pageList(@ApiParam("英文字段") String configCode) {
        List<XyOptionItemConfig> itemConfigs = xyOptionItemConfigService.findByConfigCode(configCode);
        return ResultMap.success(itemConfigs);
    }

    @ApiOperation("查询配置项列表")
    @GetMapping("/findItemByKey")
    public ResultMap<XyOptionItemConfig> findByCodeAndKey(@ApiParam("英文字段") String codeConfig,@ApiParam("参数key") String itemKey) {
        XyOptionItemConfig itemConfigs = xyOptionItemConfigService.findByCodeAndKey(codeConfig,itemKey);
        return ResultMap.success(itemConfigs);
    }

    @ApiOperation("查询广告样式列表")
    @GetMapping("/findAdTypeItem")
    public ResultMap findAdTypeItem() {
        List<XyOptionItemConfig> itemConfigs = xyOptionItemConfigService.findByConfigCode(OptionConfigConstant.AD_TYPE);
        return ResultMap.success(itemConfigs);
    }

    @ApiOperation("查询操作平台类型")
    @GetMapping("/findOperationPlatType")
    public ResultMap<List<XyOptionItemConfig>> findOperationPlatType(@ApiParam("英文字段") Integer operationPlat) {
        List<String> allConfigCode = OperationEnum.getAllType();
        String configCode = OperationEnum.getNameByType(operationPlat);
        List<XyOptionItemConfig> itemConfigs = xyOptionItemConfigService.findOperationPlatType(allConfigCode,configCode);
        return ResultMap.success(itemConfigs);
    }

    @ApiOperation("查询应用管理列表")
    @GetMapping("/findAppTypeItem")
    public ResultMap findAppTypeItem() {
        List<XyOptionItemConfig> itemConfigs = xyOptionItemConfigService.findByConfigCode(OptionConfigConstant.APP_TYPE);
        return ResultMap.success(itemConfigs);
    }

    @ApiOperation("根据广告样式查询广告平台列表")
    @GetMapping("/findPlatList")
    public ResultMap findPlatList(String adType) {
        return ResultMap.success(xyPlatService.findByAdType(adType));
    }

    @ApiOperation("查询渲染方式列表")
    @GetMapping("/findRenderList")
    public ResultMap findRenderList(String platKey,String adType) {
        return ResultMap.success(xyRenderService.findList(platKey,adType));
    }

    @ApiOperation("查询渲染方式列表")
    @GetMapping("/findAdSizeList")
    public ResultMap findAdSizeList(String adType) {
        return ResultMap.success(xyAdShapeService.findByAdType(adType));
    }

    @ApiOperation("查询渠道列表")
    @GetMapping("/findChannelList")
    public ResultMap findChannelList(String appPackage) {
        if(StringUtils.isEmpty(appPackage)){
            return ResultMap.success(Lists.newArrayList());
        }
        return ResultMap.success(mofangService.findChannelList(appPackage));
    }

    @ApiOperation("获取版本列表")
    @GetMapping("/getVersionList")
    public ResultMap getVersionList(String appPackage) {
        if(StringUtils.isEmpty(appPackage)){
            return ResultMap.success();
        }
        List<VersionVo> list = mofangService.getVersionList(appPackage);
        List<String> versionList = list.stream().map(VersionVo::getAppVersion).collect(Collectors.toList());

        return ResultMap.success(versionList);
    }

    @ApiOperation("获取分层信息")
    @GetMapping("/getLayerInfo")
    public ResultMap<List<AbLayer>> getLayerInfo(@ApiParam("包名") String appPackage, @ApiParam("实验id") Integer exp_id) {
        try {
            if (appPackage == null){
                return ResultMap.error("必须传入包名");
            }
            return ResultMap.success(abExpService.getLayerInfo(appPackage, exp_id));
        } catch (ServiceException e) {
            e.printStackTrace();

            return ResultMap.success(new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }


    @ApiOperation("媒体账号列表")
    @GetMapping("/userSelectList")
    public ResultMap<List<User>> userSelectList() {
        List<User> userList = userService.listUser();
        return ResultMap.success(userList);
    }

    @ApiOperation("获取渠道操作列表")
    @GetMapping("/getChannelOperationList")
    public ResultMap getChannelOperationList() {

        Map<String, String> map = XyConstant.CHANNEL_OPERATION_MAP;
        List<Map<String, String>> list = new ArrayList<>();
        map.forEach((key,value) -> {
            Map<String, String> item = new HashMap<>();
            item.put("key", key);
            item.put("value", value);
            list.add(item);
        });

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);
        return ResultMap.success(data);
    }

    @ApiOperation("获取渠道操作列表")
    @GetMapping("/getVersionOperationList")
    public ResultMap getVersionOperationList() {
        Map<String, String> map = new HashMap<>();
        map.putAll(XyConstant.VERSION_OPERATION_MAP);

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> defaultItem = new HashMap<>();
        defaultItem.put("key", XyConstant.OPERA_ALL);
        defaultItem.put("value", map.get(XyConstant.OPERA_ALL));
        list.add(defaultItem);
        map.remove(XyConstant.OPERA_ALL);
        map.forEach((key,value) -> {
            Map<String, String> item = new HashMap<>();
            item.put("key", key);
            item.put("value", value);
            list.add(item);
        });

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);
        return ResultMap.success(data);
    }
}
