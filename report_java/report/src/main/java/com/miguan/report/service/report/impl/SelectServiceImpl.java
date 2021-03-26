package com.miguan.report.service.report.impl;

import com.google.common.collect.Maps;
import com.miguan.report.common.enums.*;
import com.miguan.report.common.util.AppNameUtil;
import com.miguan.report.dto.SelectListDto;
import com.miguan.report.mapper.SelectMapper;
import com.miguan.report.service.report.SelectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.miguan.report.common.constant.CommonConstant.UNDERLINE_SEQ;

/**
 * @Description 下拉列表service
 * @Author zhangbinglin
 * @Date 2020/6/17 18:38
 **/
@Service
public class SelectServiceImpl implements SelectService {

    @Resource
    private SelectMapper selectMapper;

    /**
     * 下拉列表-详情页
     * @param appType 类型：1=西柚视频,2=炫来电
     * @return
     */
    public SelectListDto detailList(Integer appType) {
        appType = (appType == null ? 1 : appType);
        SelectListDto selectListDto = new SelectListDto();
        selectListDto.setAppList(selectMapper.appList(appType));  //app列表
        selectListDto.setClientList(ClientEnum.getClientList());  //客户端列表
        selectListDto.setAppClientList(AppClientEnum.getAppClientList(appType));  //app客户端列表
        selectListDto.setAppClientTabList(AppClientTabEnum.getAppClientTabList(appType));  //应用客户端（活跃用户报表tab页使用）
        selectListDto.setAppWithDeviceList(buildAppWithDevice());  //app客户端列表
        selectListDto.setPlatformList(PlatFormEnum.getPlatFormList());  //平台列表
        selectListDto.setBannerPositionList(selectMapper.bannerPositionList(appType));  //广告位列表
        return selectListDto;
    }

    /**
     * 下拉选项-获取代码为ID列表
     * @param keyword 关键字
     * @param appClientId 应用客户端
     * @param platForm 平台
     * @param totalName 广告位置名称
     * @param appType 类型：1=西柚视频,2=炫来电
     * @return
     */
    public List<String> getAdSpaceIdList(String keyword, String appClientId, String platForm, String totalName, Integer appType) {
        List<String> appClientIds = null;
        List<String> platForms = null;
        List<String> totalNames = null;
        if (StringUtils.isNotBlank(appClientId)) {
            appClientIds = Arrays.asList(appClientId.split(","));
        }
        if (StringUtils.isNotBlank(platForm)) {
            platForms = Arrays.asList(platForm.split(","));
        }
        if (StringUtils.isNotBlank(totalName)) {
            totalNames = Arrays.asList(totalName.split(","));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("appClientIds", appClientIds);
        params.put("platForms", platForms);
        params.put("totalNames", totalNames);
        params.put("appType", appType);
        return selectMapper.getAdSpaceIdList(params);
    }

    private List<Map<String, String>> buildAppWithDevice() {
        List<Map<String, String>> list = Stream.of(AppVideoPackageEnum.values())
                .map(e -> {
                    AppEnum app = e.getAppEnum();
                    ClientEnum client = e.getClientEnum();
                    Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
                    map.put("id", new StringBuilder().append(app.getId()).append(UNDERLINE_SEQ).append(client.getId()).toString());
                    map.put("name", AppNameUtil.convertDeviceType2name(app.getId(), client.getId()));
                    return map;
                }).collect(Collectors.toList());
        List<Map<String, String>> list2 = Stream.of(AppLaiDianPackageEnum.values()).map(e -> {
            AppEnum app = e.getAppEnum();
            ClientEnum client = e.getClientEnum();
            Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
            map.put("id", new StringBuilder().append(app.getId()).append(UNDERLINE_SEQ).append(client.getId()).toString());
            map.put("name", AppNameUtil.convertDeviceType2name(app.getId(), client.getId()));
            return map;
        }).collect(Collectors.toList());
        list.addAll(list2);
        return list;
    }
}
