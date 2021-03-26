package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.common.collect.Maps;
import com.miguan.reportview.dto.AdErrorDto;
import com.miguan.reportview.mapper.DwAdErrorMapper;
import com.miguan.reportview.service.IAdErrorService;
import com.miguan.reportview.vo.AdErrorDataVo;
import com.miguan.reportview.vo.ParamsBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
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
@DS("clickhouse")
public class AdErrorServiceImpl implements IAdErrorService {

    @Resource
    private DwAdErrorMapper dwAdErrorMapper;

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
                                                 List<String> modeles,
                                                 List<String> appAdspace,
                                                 List<String> groups,
                                                 Integer appType,
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
        return ParamsBuilder.builder(14)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("appType", appType)
                .put("showType", isNew == null ? null : isNew ? 1 : 0)
                .put("appPackages", appPackages)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("spaceKeys", spaceKeys)
                .put("adcodes", adcodes)
                .put("plates", plates)
                .put("modeles", modeles)
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
                return "is_new";
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
                return "model";
            }
            if ("8".equals(e)) {
                return "msg";
            }
            if ("9".equals(e)) {
                return "father_channel";
            }
            if ("10".equals(e)) {
                return "ad_source";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdErrorDto> getData(Boolean isNew,
                                    List<String> appPackages,
                                    List<String> appVersions,
                                    List<String> pChannelIds,
                                    List<String> channelIds,
                                    List<String> spaceKeys,
                                    List<String> adcodes,
                                    List<String> plates,
                                    List<String> modeles,
                                    List<String> appAdspace,
                                    List<String> groups,
                                    Integer appType,
                                    String startDate,
                                    String endDate) {

        Map<String, Object> params = buildQueryParams(isNew, appPackages, appVersions, pChannelIds, channelIds, spaceKeys, adcodes, plates, modeles, appAdspace, groups, appType, startDate, endDate);
        List<AdErrorDataVo> list = dwAdErrorMapper.getData(params);
        if (isEmpty(list)) {
            return null;
        }
        if (!isEmpty(groups) && groups.contains("8")) {
            Map<String, Double> totalErrNumMap = new HashMap<>();
            for(AdErrorDataVo vo : list) {
                double totalErrNum = totalErrNumMap.get(vo.getDd()) == null ? 0 : totalErrNumMap.get(vo.getDd());
                totalErrNum = totalErrNum + vo.getErrCount();
                totalErrNumMap.put(vo.getDd(), totalErrNum);
            }
//            double totalErrNum = list.stream().mapToDouble(AdErrorDataVo::getErrCount).sum();
            return list.stream().map(e -> this.convertDto(e, totalErrNumMap)).collect(Collectors.toList());
        }
        return list.stream().map(e -> this.convertDto(e, null)).collect(Collectors.toList());
    }

    private AdErrorDto convertDto(AdErrorDataVo vo, Map<String, Double> totalErrNumMap) {
        double totalErrNum = 0;
        if(totalErrNumMap != null) {
            totalErrNum = totalErrNumMap.get(vo.getDd()) == null ? 0 : totalErrNumMap.get(vo.getDd());
        }
        AdErrorDto dto = new AdErrorDto();
        dto.setMsg(vo.getMsg());
        dto.setErrNum(vo.getErrCount());
        dto.setErrRate(divide(vo.getErrCount(), vo.getReqCount(), true));
        dto.setErrUser(vo.getDeviceCount());
        dto.setErrUserRate(divide(vo.getDeviceCount(), vo.getActiveUser(), true));
        dto.setReqNum(vo.getReqCount());
        dto.setErrPerc(divide(vo.getErrCount(), totalErrNum, true));
        dto.setDate(vo.getDd());
        dto.setPackageName(vo.getPackageName());
        dto.setAppVersion(vo.getAppVersion());
        dto.setIsNew(vo.getIsNew());
        dto.setChannel(vo.getChannel());
        dto.setAdSpace(vo.getAdKey());
        dto.setAdCode(vo.getAdId());
        dto.setModel(vo.getModel());
        dto.setFatherChannel(vo.getFatherChannel());
        dto.setFlat(vo.getAdSource());
        return dto;
    }
}
