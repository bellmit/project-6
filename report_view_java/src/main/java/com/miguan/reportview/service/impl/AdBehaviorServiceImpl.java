package com.miguan.reportview.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.common.collect.Maps;
import com.miguan.reportview.common.enmus.PlatEnmu;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.AdPlat;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.mapper.DwAdActionsMapper;
import com.miguan.reportview.service.*;
import com.miguan.reportview.vo.AdBehaviorDataVo;
import com.miguan.reportview.vo.AdBehaviorSonDataVo;
import com.miguan.reportview.vo.AdTotalVo;
import com.miguan.reportview.vo.ParamsBuilder;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
import static com.miguan.reportview.common.utils.NumCalculationUtil.roundHalfUpDouble;
import static org.apache.commons.collections4.CollectionUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * <p>
 * 视频数据宽表 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
@Service
@Slf4j
@DS("clickhouse")
public class AdBehaviorServiceImpl implements IAdBehaviorService {

    @Resource
    private DwAdActionsMapper dwAdActionsMapper;

    @Autowired
    private IBannerDataTotalNameService bannerDataTotalNameService;

    @Resource
    private IAppService iAppService;

    @Resource
    private IAdPlatService iAdPlatService;

    @Resource
    private IAdAdvertService iAdAdvertService;

    @Resource
    private RedisService redisService;

    /**
     *
     * @param isNew
     * @param appPackages
     * @param appVersions
     * @param channelIds
     * @param groups 1=应用2=版本3=新老用户4=渠道
     * @param startDate
     * @param endDate
     * @return
     */
    private Map<String, Object> buildQueryParams(Boolean isNew,
                                                 List<String> appPackages,
                                                 List<String> appVersions,
                                                 List<String> pChannelIds,
                                                 List<String> channelIds,
                                                 List<String> spaceKeys,
                                                 List<String> adcodes,
                                                 List<String> plates,
                                                 List<String> appAdspace,
                                                 List<String> groups,
                                                 String startDate,
                                                 String endDate) {
        List<Map<String, String>> appAdspaces = null;
        if (!isEmpty(appAdspace)) {
            appAdspaces = appAdspace.stream().map(e -> {
                String[] t = e.split("::");
                Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
                map.put("appPackage", t[0]);
                map.put("spaceKey", t[1]);
                return map;
            }).collect(Collectors.toList());
        }
        List<String> groupColums = buildGroupColum(groups);
        return ParamsBuilder.builder(13)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("showType", isNew == null ? null : isNew ? 1 : 0)
                .put("appPackages", appPackages)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("spaceKeys", spaceKeys)
                .put("adcodes", adcodes)
                .put("plates", plates)
                .put("appAdspaces", appAdspaces)
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .get();
    }

    /**
     *
     * @param groups 1=应用2=版本3=新老用户4=渠道
     * @return
     */
    private List<String> buildGroupColum(List<String> groups) {
        if (isEmpty(groups)) {
            return null;
        }
        return groups.stream().map(e -> {
            if ("1".equals(e)) {
                return "package_name";
            }
            if ("2".equals(e)) {
                return "app_version";
            }
            if ("3".equals(e)) {
                return "is_new_app";
            }
            if ("4".equals(e)) {
                return "channel";
            }
            if ("5".equals(e)) {
                return "ad_key";
            }
            if ("6".equals(e)) {
                return "ad_id";
            }
            if ("7".equals(e)) {
                return "father_channel";
            }
            if ("8".equals(e)) {
                return "ad_source";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdBehaviorDto> getData(Boolean isNew,
                                       List<String> appPackages,
                                       List<String> appVersions,
                                       List<String> pChannelIds,
                                       List<String> channelIds,
                                       List<String> spaceKeys,
                                       List<String> adcodes,
                                       List<String> plates,
                                       List<String> appAdspace,
                                       List<String> groups,
                                       int appType,
                                       String startDate,
                                       String endDate) {
        Map<String, Object> params = buildQueryParams(isNew, appPackages, appVersions, pChannelIds, channelIds, spaceKeys, adcodes, plates, appAdspace, groups, startDate, endDate);
        params.put("appType", appType);
        List<AdBehaviorDataVo> list = dwAdActionsMapper.getNewData(params);
        if (isEmpty(list)) {
            return null;
        }
        //有些条件或分组参与查询时不能查询到营收
        boolean qnpdatabase = false;
        if (isNew == null && isEmpty(appVersions) && isEmpty(channelIds) && isEmpty(adcodes)) {
            qnpdatabase = true;
        }
        if (qnpdatabase && (isEmpty(groups) || !containsAny(groups, "2", "3", "4", "6", "7"))) {
            List<AdTotalVo> list2 = bannerDataTotalNameService.getData(appPackages, spaceKeys, params.get("appAdspaces"), plates, startDate, endDate, appType, groups);
            return list.stream().map(e -> this.convertDto(e, isEmpty(list2) ? null : list2)).collect(Collectors.toList());
        } else {
            return list.stream().map(e -> this.convertDto(e, null)).collect(Collectors.toList());
        }
    }

    private AdBehaviorDto convertDto(AdBehaviorDataVo vo, List<AdTotalVo> list2) {
        AdBehaviorDto dto = new AdBehaviorDto();
        dto.setReqNum(vo.getReq());
        dto.setResNum(vo.getRes());
        dto.setFillRate(divide(vo.getRes(), vo.getReq(), true));
        dto.setShowNum(vo.getVshow());
        dto.setShowRate(divide(vo.getVshow(), vo.getRes(), true));
        dto.setClickNum(vo.getVclick());
        dto.setClickRate(divide(vo.getVclick(), vo.getVshow(), true));
        dto.setShowUser(vo.getVshowUser());
        dto.setPerShowNum(divide(vo.getVshow(), vo.getActiveUser(), false));
        dto.setPerClickNum(divide(vo.getVclick(), vo.getActiveUser(), false));
        dto.setUclickRate(divide(vo.getClickUser(), vo.getActiveUser(), true));
        if (list2 != null) {
            Optional<AdTotalVo> vo2 = list2.stream().filter(e ->{
                boolean isPlat = true;
                if(e.getPlatForm() == null && StringUtils.isBlank(vo.getAdSource())){

                }else{
                     isPlat =  (e.getPlatForm() != null && PlatEnmu.getEnum(e.getPlatForm().intValue()) != null && PlatEnmu.getEnum(e.getPlatForm().intValue()).name().equals(vo.getAdSource()));
                }
               return trimToEmpty(e.getAppPackage()).equals(trimToEmpty(vo.getPackageName()))
                        && trimToEmpty(e.getPositionType()).equals(trimToEmpty(vo.getAdKey()))
                        && trimToEmpty(e.getDd()).equals(trimToEmpty(vo.getDd()))
                        && isPlat;
                    }

            ).findFirst();
            if (vo2.isPresent()) {
                dto.setRevenue(vo2.get().getRevenue());
                dto.setCpm(vo.getVshow() == 0 ? 0 : roundHalfUpDouble(vo2.get().getRevenue() / vo.getVshow() * 1000));
            }
        } else {
            dto.setRevenue(0.0);
            dto.setCpm(0.0);
        }
        dto.setDate(vo.getDd());
        dto.setPackageName(vo.getPackageName());
        dto.setAppVersion(vo.getAppVersion());
        dto.setIsNew(vo.getIsNew());
        dto.setChannel(vo.getChannel());
        dto.setAdSpace(vo.getAdKey());
        dto.setAdCode(vo.getAdId());
        dto.setFatherChannel(vo.getFatherChannel());
        dto.setFlat(vo.getAdSource());
        dto.setStock(vo.getStock());
        dto.setVclick(vo.getZoneClick());
        dto.setVshow(vo.getZoneShow());
        return dto;
    }

    /**
     * 子广告报表数据
     * @param appPackage 包名
     * @param adKey 广告位key
     * @param adSource 平台
     * @param renderType  渲染方式
     * @param adcType  素材
     * @param qId  子广告位
     * @param appType  app类型,1:视频，2：来电
     * @return
     */
    public List<AdBehaviorSonDto> getSonData(List<String> appPackage, List<String> adKey, List<String> adSource, List<String> renderType,
                                                             List<String> adcType, List<String> qId, Integer appType, String startDate, String endDate) {
        Map<String, Object> params = buildSonQueryParams(appPackage, adKey, adSource, renderType, adcType, qId, appType, startDate, endDate);
        List<AdBehaviorSonDataVo> list = dwAdActionsMapper.getSonData(params);

        List<AdBehaviorSonDto> result = new ArrayList<>();
        List<AdPlat> adPlats = iAdPlatService.getAllPlat();
        list.forEach(r->{
            result.add(this.convertSonDto(r, adPlats));
        });
        return result;
    }

    private Map<String, Object> buildSonQueryParams(List<String> appPackage,List<String> adKey, List<String> adSource,List<String> renderType,
                                                    List<String> adcType,List<String> qId, Integer appType, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appPackages", appPackage);
        params.put("adKeys", adKey);
        params.put("adSources", adSource);
        params.put("renderTypes", renderType);
        params.put("appType", appType);
        List<String> image = Arrays.asList("小图（1图2文）","大图（2图2文）","组图（3小图）","大图","小图","图片","竖版图片","大图","组图","未知类型");
        List<String> video = Arrays.asList("竖版视频","视频");
        List<String> adcTypeNew = new ArrayList<>();
        if(adcType != null) {
            adcType.forEach(r->{
                if(r.equals("图片")) {
                    adcTypeNew.addAll(image);
                }
                if(r.equals("视频")) {
                    adcTypeNew.addAll(video);
                }
            });
            params.put("adcTypes", adcTypeNew);
        }
        List<String> paramsQid = new ArrayList<>();
        if(qId == null) {
            paramsQid.add("");
        } else {
            qId.forEach(r -> {
                if(r.contains("><")) {
                    r = "BETWEEN " + r.replace("><", " AND ");
                }
                paramsQid.add(r);
            });
        }
        params.put("qIds", paramsQid);
        return params;
    }

    private AdBehaviorSonDto convertSonDto(AdBehaviorSonDataVo asdv, List<AdPlat> adPlats) {
        AdBehaviorSonDto dto = new AdBehaviorSonDto();
        BeanUtils.copyProperties(asdv, dto);

        App app = iAppService.getAppByPackageName(asdv.getPackageName());
        if(app != null) {
            dto.setPackageName(app.getName());  //应用名称
        }
        dto.setAdSource(iAdPlatService.getPlatName(adPlats, asdv.getAdSource()));  //平台名称
        dto.setAdPostion(iAdAdvertService.getAdSpaceNname(asdv.getAdPostion()));  //广告位
        dto.setShowRate(divide(asdv.getShowNum(), asdv.getReqNum(), true));   //展示率=展示量/的请求量
        dto.setClickRate(divide(asdv.getClickNum(), asdv.getShowNum(), true));   //点击率=点击量/展示量
        dto.setPershowUserR(divide(asdv.getShowNum(), asdv.getShowUser(), false));  //人均展示量=展示量之和/展现用户数
        dto.setShowUserRate(divide(asdv.getShowUser(), asdv.getActiveUser(), true));  //展现用户占比=展现用户数/日活用户
        return dto;
    }

    /**
     * 钉钉-广告展示/广告库存比值预警
     * @return
     */
    public String findEarlyWarnList() {
        Map<String, Object> params = new HashMap<>();
        params.put("dd", DateUtil.dateStr2(new Date()));
        params.put("startTime", DateUtil.dateStr4(DateUtil.rollMinute(new Date(), -30)));
        params.put("endTime", DateUtil.dateStr4(new Date()));

        //广告位与库存比值预警
        params.put("type", 1);
        List<EarlyWarningDto> list = dwAdActionsMapper.findEarlyWarnList(params);
        String result1 = earlyWarnString(1, list);

        //广告位与库存比值降幅预警
        params.put("type", 2);
        List<EarlyWarningDto> list2 = findDecreaseEarlyWarnList(params);
        String result2 = earlyWarnString(2, list2);

        //广告位库存为0预警
        params.put("type", 3);
        list = dwAdActionsMapper.findEarlyWarnList(params);
        String result3 = earlyWarnString(3, list);

        return result1 + result2 + result3;
    }

    private List<EarlyWarningDto> findDecreaseEarlyWarnList(Map<String, Object> params) {
        List<EarlyWarningDto> list = dwAdActionsMapper.findEarlyWarnList(params);  //查询前半个小时的 广告位与库存比值

        String lastJson =redisService.get("earlyWarnList");  //从缓存查询出上个半小时的 广告位与库存比值
        String json = JSON.toJSONString(list);
        redisService.set("earlyWarnList", json, 120 * 60);
        if(StringUtil.isBlank(lastJson)) {
            return null;
        }
        log.info("广告位与库存比值降幅预警上次的值：{}", json);
        log.info("广告位与库存比值降幅预警这次的值：{}", list);


        JSONArray jsonArray = JSONArray.parseArray(lastJson);
        Map<String, Double> lastMap = new HashMap<>();
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject one = jsonArray.getJSONObject(i);
            lastMap.put(one.getString("packageNameZw") + one.getString("adKeyName"), one.getDoubleValue("showStockRate"));
        }

        List<EarlyWarningDto> result = new ArrayList<>();
        for(EarlyWarningDto dto : list) {
            String packageNameZw = dto.getPackageNameZw();
            String adKeyName = dto.getAdKeyName();
            double lastValue = lastMap.get(packageNameZw + adKeyName) == null ? 0 : lastMap.get(packageNameZw + adKeyName);
            double cha = lastValue - dto.getShowStockRate();
            if(cha > 15) {
                //上个时间点计算比值 - 本次计算比值 ＞ 15%
                EarlyWarningDto one = new EarlyWarningDto();
                one.setPackageNameZw(dto.getPackageNameZw());
                one.setAdKeyName(dto.getAdKeyName());
                one.setShowStockRate(cha);
                result.add(one);
            }
        }
        //根据showStockRate 降序
        result = result.stream().sorted(Comparator.comparing(EarlyWarningDto::getShowStockRate).reversed()).collect(Collectors.toList());
        return result;
    }

    /**
     * 把结果转成字符串
     * @param type 1:广告位与库存比值预警 2:广告位与库存比值降幅预警3:广告位库存为0
     * @param list
     * @return
     */
    private String earlyWarnString(int type, List<EarlyWarningDto> list) {
        if(list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        if(type == 1) {
            result.append("【广告位与库存比值预警】\n");
        } else if(type == 2) {
            result.append("【广告位与库存比值降幅预警】\n");
        } else if(type == 3) {
            result.append("【广告位库存为0】\n");
        }
        for(EarlyWarningDto dto : list) {
            result.append(dto.getPackageNameZw() + "_" + dto.getAdKeyName());
            if(type == 1) {
                result.append(" 比值为：" + roundHalfUpDouble(2,dto.getShowStockRate()) + "%\n");
            } else if(type == 2) {
                result.append(" 降幅为：" + roundHalfUpDouble(2,dto.getShowStockRate()) + "%\n");
            } else if(type == 3) {
                result.append("\n");
            }
        }
        return result.toString();
    }
}
