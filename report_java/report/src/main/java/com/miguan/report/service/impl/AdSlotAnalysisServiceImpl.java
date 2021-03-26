package com.miguan.report.service.impl;

import com.google.common.collect.Maps;
import com.miguan.report.mapper.AdSlotAnalysisMapper;
import com.miguan.report.service.report.AdSlotAnalysisService;
import com.miguan.report.vo.AdSlotAnlyDetailVo;
import com.miguan.report.vo.AdSlotAnlyVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.miguan.report.common.constant.CommonConstant.UNDERLINE_SEQ;
import static com.miguan.report.common.util.AppNameUtil.convertDeviceType2name;

/**广告位分析服务接口实现逻辑
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Service
public class AdSlotAnalysisServiceImpl implements AdSlotAnalysisService {
    @Resource
    private AdSlotAnalysisMapper adSlotAnalysisMapper;

    @Override
    public List<AdSlotAnlyVo> loadByAppNameAndAdspace(String qdate, int platType, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(3);
        sqlparams.put("qdate", qdate);
        if (platType != 0) {
            sqlparams.put("platType", platType);
        }
        sqlparams.put("appType", appType);
        List<AdSlotAnlyVo> radata = adSlotAnalysisMapper.queryGroupByAppNameAndAdspace(sqlparams);
        if (CollectionUtils.isEmpty(radata)) {
            return null;
        }
        return radata;
    }

    @Override
    public List<AdSlotAnlyDetailVo> loadAdSlotDetail(String adSpace, String startDate, String endDate, List<String> apps, int platType, int appType) {
        List<String[]> qapps = apps.stream().map(n -> n.split(UNDERLINE_SEQ)).collect(Collectors.toList());
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(6);
        sqlparams.put("adSpace", adSpace);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("apps", qapps);
        if (platType != 0) {
            sqlparams.put("platType", platType);
        }
        sqlparams.put("appType", appType);
        List<AdSlotAnlyDetailVo> adData = adSlotAnalysisMapper.queryAdSlotDetail(sqlparams);
        if (CollectionUtils.isEmpty(adData)) {
            return null;
        }
        return adData.stream().map(e -> {
            e.setAppName(convertDeviceType2name(e.getAppName(), e.getAppDevType()));
            return e;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdSlotAnlyDetailVo> loadAdUserBehaviorDetail(String adSpace, String startDate, String endDate, List<String> apps, int appType) {
        List<String[]> qapps = apps.stream().map(n -> n.split(UNDERLINE_SEQ)).collect(Collectors.toList());
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(5);
        sqlparams.put("adSpace", adSpace);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("apps", qapps);
        sqlparams.put("appType", appType);
        List<AdSlotAnlyDetailVo> adData = adSlotAnalysisMapper.queryAdUserBehaviorDetail(sqlparams);
        if (CollectionUtils.isEmpty(adData)) {
            return null;
        }
        return adData.stream().map(e -> {
            e.setAppName(convertDeviceType2name(e.getAppName(), e.getAppDevType()));
            return e;
        }).collect(Collectors.toList());
    }

}
