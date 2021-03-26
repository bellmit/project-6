package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.util.NumCalculationUtil;
import com.miguan.bigdata.mapper.DspPlanMapper;
import com.miguan.bigdata.service.DspDataService;
import com.miguan.bigdata.vo.UserRatioVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tool.util.BigDecimalUtil;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

import static com.miguan.bigdata.common.util.NumCalculationUtil.roundHalfUpDouble;

/**
 * DSP系统数据serviceImpl
 */
@Slf4j
@Service
public class DspDataServiceImpl implements DspDataService {

    @Resource
    private DspPlanMapper dspPlanMapper;

    /**
     * 统计近7天每个时间段的日活数占比
     * @return
     */
    public LinkedHashMap<String, Double> getUserRatio() {
        String startDay = DateUtil.dateStr7(DateUtil.rollDay(new Date(),-7));  //7天前日期
        String endDay = DateUtil.dateStr7(DateUtil.rollDay(new Date(),-1));  //昨天日期
        String now = DateUtil.dateStr7(new Date());  //今天日期
        String time = currentTimeSlot();  //当前时间片段

        Map<String, Object> params = new HashMap<>();
        params.put("startDay", Integer.parseInt(startDay));
        params.put("endDay", Integer.parseInt(endDay));
        params.put("time", time);
        params.put("dt", Integer.parseInt(now));
        int count = dspPlanMapper.countTimeSlotActiveUse(params); //查询当天的time_slot_active_user（近7天每个时间段的活跃用户数）表有没数据
        if(count == 0) {
            //当天的time_slot_active_user没数据，则先统计
            log.info("当天的time_slot_active_user没数据，则先统计, time:{}", time);
            dspPlanMapper.insertTimeSlotActiveUser(params);
        }
        List<UserRatioVo> list = dspPlanMapper.getUserRatio(params);  //获取近7天每个时间段（30分钟一个时间段）的日活用户数
        log.info("获取近7天每个时间段的日活用户数:{}, time:{}", list, time);
        int totalUser = 0;  //总用户数
        for(UserRatioVo ratio :list) {
            totalUser+=ratio.getActiveUser();
        }

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        double totalRatio = 0D;
        for(UserRatioVo vo :list) {
            double ratio = roundHalfUpDouble(6, vo.getActiveUser()/Double.valueOf(totalUser));
            if("23:30".equals(vo.getTimeSlot())) {
                ratio = BigDecimalUtil.sub(1D, totalRatio); //最后一个时间段的比例，用1减掉前面比例总和，防止总和加起来不等于1
            }
            result.put(vo.getTimeSlot(), ratio);
            totalRatio = BigDecimalUtil.add(totalRatio, ratio);
        }
        log.info("获取近7天每个时间段的日活用户数结果:{}, time:{}", result, time);
        return result;
    }

    /**
     * 获取当前时间的时间段，例如：12:30
     * @return
     */
    private String currentTimeSlot() {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int slot = minute / 30;
        String hour = DateUtil.dateStr(calendar.getTime(), "HH:");
        return (slot == 0 ? hour + "00" : hour + "30");
    }
}
