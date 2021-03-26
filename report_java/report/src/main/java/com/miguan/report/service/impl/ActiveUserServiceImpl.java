package com.miguan.report.service.impl;

import com.miguan.report.common.constant.ActiveUserConstant;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.dto.PairLineDataDto;
import com.miguan.report.dto.PairLineDto;
import com.miguan.report.mapper.ActiveUserMapper;
import com.miguan.report.mapper.CpmMapper;
import com.miguan.report.service.ActiveUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 活跃用户service
 * @Author zhangbinglin
 * @Date 2020/6/17 14:10
 **/
@Service
public class ActiveUserServiceImpl implements ActiveUserService {

    @Resource
    private ActiveUserMapper activeUserMapper;

    /**
     * 统计活跃用户比对报表数据(双Y轴折线图使用)
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @return
     */
    public PairLineDto countActiveUserLineChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                                Integer isContrast, String appId, Integer rightStatItem) {
        PairLineDto pairLineDto = new PairLineDto();
        List<LineChartDto> baseDatas = this.countActiveUserChart(startDate, endDate, appType, leftStatItem, isContrast, appId, rightStatItem);
        LinkedHashMap<String, List<LineChartDto>> mapData = new LinkedHashMap<>();
        LinkedHashSet<String> dates = new LinkedHashSet<>();
        for (LineChartDto lcd : baseDatas) {
            String key = lcd.getType() + "," + lcd.getSortTag();
            List<LineChartDto> list = mapData.get(key) == null ? new ArrayList<>() : mapData.get(key);
            list.add(lcd);
            mapData.put(key, list);
            dates.add(lcd.getDate());
        }
        pairLineDto.setDates(dates);

        List<PairLineDataDto> lineDatas = new ArrayList<>();
        int index = 0;
        for (String key : mapData.keySet()) {
            PairLineDataDto pairLineDataDto = new PairLineDataDto();
            pairLineDataDto.setName(key.split(",")[0]);
            if (isContrast == ActiveUserConstant.IS_CONTRAST_YES && index > 0) {
                //选择了对比后，第二天折线图的Y轴使用另外的坐标
                pairLineDataDto.setYAxisIndex(1);
            }
            List<LineChartDto> lineChartDtoList = mapData.get(key);
            List<Double> data = lineChartDtoList.stream().map(line -> line.getValue()).collect(Collectors.toList()); //每日数据数组
            pairLineDataDto.setData(data);
            lineDatas.add(pairLineDataDto);
            index++;
        }
        pairLineDto.setLineDatas(lineDatas);
        return pairLineDto;
    }

    /**
     * 统计活跃用户比对报表数据
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @return
     */
    public List<LineChartDto> countActiveUserChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                                   Integer isContrast, String appId, Integer rightStatItem) {
        return this.countActiveUserChart(startDate, endDate, appType, leftStatItem, isContrast, appId, rightStatItem, null);
    }

    /**
     * 统计活跃用户比对报表数据
     *
     * @param startDate     统计开始时间
     * @param endDate       统计结束时间
     * @param appType       app类型：1=西柚视频,2=炫来电
     * @param leftStatItem  左统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param isContrast    是否对比，0=否，1=是
     * @param appId         appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)
     * @param rightStatItem 右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比
     * @param detailType 等于1，则表示是明细表格接口进来
     * @return
     */
    public List<LineChartDto> countActiveUserChart(String startDate, String endDate, Integer appType, Integer leftStatItem,
                                                      Integer isContrast, String appId, Integer rightStatItem, Integer detailType) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appType", appType);
        params.put("statItem", leftStatItem);
        params.put("isContrast", isContrast);
        params.put("detailType", detailType);
        params.put("appId", appId);
        List<LineChartDto> resultList = new ArrayList<>();

        if (ActiveUserConstant.IS_CONTRAST_NO == isContrast) {
            //不需要比对，则直接查询出全部数据
            List<LineChartDto> oneList = new ArrayList<>();
            List<LineChartDto> twoList = new ArrayList<>();
            if(appType == 1) {
                //炫来电不需要汇总数据
                oneList = activeUserMapper.countTotalActiveUserList(params);
            }
            twoList = activeUserMapper.countActiveUserList(params);
            resultList.addAll(oneList);
            resultList.addAll(twoList);
            Collections.sort(resultList);
        } else {
            List<LineChartDto> leftList = null;
            List<LineChartDto> rightList = new ArrayList<>();
            params.put("appId", appId);
            params.put("statItem", leftStatItem);
            if(StringUtils.isNotBlank(appId)) {
                leftList = activeUserMapper.countActiveUserList(params);
                if(rightStatItem != null) {
                    params.put("statItem", rightStatItem);
                    rightList = activeUserMapper.countActiveUserList(params);
                }
                resultList.addAll(leftList);
                resultList.addAll(rightList);
                Collections.sort(resultList);
            } else {
                leftList = activeUserMapper.countTotalActiveUserList(params);
                if(rightStatItem != null) {
                    params.put("statItem", rightStatItem);
                    rightList = activeUserMapper.countTotalActiveUserList(params);
                }
                resultList.addAll(leftList);
                resultList.addAll(rightList);
                Collections.sort(resultList);
            }
        }
        return resultList;
    }


}
