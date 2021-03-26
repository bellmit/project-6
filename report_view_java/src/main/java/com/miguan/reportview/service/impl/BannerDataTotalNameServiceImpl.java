package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.common.enmus.PlatEnmu;
import com.miguan.reportview.mapper.BannerDataTotalNameMapper;
import com.miguan.reportview.service.IBannerDataTotalNameService;
import com.miguan.reportview.vo.AdTotalVo;
import com.miguan.reportview.vo.ParamsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 数据汇总服务接口实现
 *
 * @author zhongli
 * @since 2020-08-07 17:29:48
 * @description
 */
@RequiredArgsConstructor
@Service
@DS("report-db")
public class BannerDataTotalNameServiceImpl implements IBannerDataTotalNameService {
    private final BannerDataTotalNameMapper bannerDataTotalNameMapper;

    @Override
    public List<AdTotalVo> getData(List<String> appPackages,
                                   List<String> spaceKeys,
                                   Object appAdspace,
                                   List<String> plates,
                                   String startDate,
                                   String endDate,
                                   int appType,
                                   List<String> groups) {
        List<Integer> platesT = isEmpty(plates) ? null : plates.stream().map(e -> PlatEnmu.valueOf(e).getCode()).collect(Collectors.toList());
        List<String> groupColums = buildGroupColum(groups);
        Map<String, Object> params = ParamsBuilder.builder(8)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("appPackages", appPackages)
                .put("spaceKeys", spaceKeys)
                .put("appAdspaces", appAdspace)
                .put("plates", platesT)
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .put("appType", appType)
                .get();
        return bannerDataTotalNameMapper.getData(params);
    }

    @Override
    public List<AdTotalVo> getData(String startDate, String endDate, int appType) {
        return this.getData(null, null, null, null, startDate, endDate, appType, null);
    }

    private List<String> buildGroupColum(List<String> groups) {
        if (isEmpty(groups)) {
            return null;
        }
        return groups.stream().map(e -> {
            if ("1".equals(e)) {
                return "app_package";
            }
            if ("5".equals(e)) {
                return "position_type";
            }
            if ("8".equals(e)) {
                return "plat_form";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }
}