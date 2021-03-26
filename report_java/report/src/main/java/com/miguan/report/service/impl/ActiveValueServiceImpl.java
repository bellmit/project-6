package com.miguan.report.service.impl;

import com.google.common.collect.Maps;
import com.miguan.report.mapper.ActiveValueMapper;
import com.miguan.report.service.report.ActiveValueService;
import com.miguan.report.vo.ActiveValueVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**日活均价值服务
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Service
public class ActiveValueServiceImpl implements ActiveValueService {
    @Resource
    private ActiveValueMapper activeValueMapper;

    /**
     * 按应用或平台分组统计
     * doc {@link ActiveValueServiceImpl#loadStaData(String, String, String, int, int, int)}
     */
    @Override
    public List<ActiveValueVo> loadStaData(String startDate, String endDate, String adSpace, int showDateType, int groupType, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(6);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("showDateType", showDateType);
        sqlparams.put("groupType", groupType);
        sqlparams.put("appType", appType);
        sqlparams.put("adSpace", adSpace);
        List<ActiveValueVo> sqldata = activeValueMapper.queryStaGroupByAppOrPlat(sqlparams);
        if (CollectionUtils.isEmpty(sqldata)) {
            return null;
        }
        if (groupType == 2) {
            return sqldata.stream().map(v -> v.convertPlatType2name()).collect(Collectors.toList());
        }else{
            return sqldata.stream().map(v -> v.convertDeviceType2name()).collect(Collectors.toList());
        }
    }

}
