package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.mapper.MonitorMapper;
import com.miguan.bigdata.service.MonitorService;
import com.miguan.bigdata.vo.MonitorVo;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 监测service
 * @Author zhangbinglin
 * @Date 2020/11/6 8:59
 **/
@Service
public class MonitorServiceImpl implements MonitorService {

    @Resource
    private MonitorMapper monitorMapper;
    @Resource
    private RedisClient redisClient;

    /**
     * 监测广告行为，用户行为中uuid为空的情况
     * @return
     */
    public String getUuidNullMonitor() {
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        params.put("dd", dd);
        params.put("startTime", DateUtil.dateStr4(DateUtil.rollMinute(new Date(), -30)));
        params.put("endTime", DateUtil.dateStr4(new Date()));
        params.put("dt", Integer.parseInt(dd.replace("-", "")));
        String adUuidThreshold = redisClient.get("adUuidThreshold");  //广告空uuid监测阀值
        if(StringUtil.isBlank(adUuidThreshold)) {
            adUuidThreshold = "10";  //默认阀值
            redisClient.set("adUuidThreshold", adUuidThreshold);  //阀值存redis中，下次要修改阀值的话，直接修改redis中的adUuidThreshold的值
        }
        String userUuidThreshold = redisClient.get("userUuidThreshold");  //用户行为空uuid监测阀值
        if(StringUtil.isBlank(userUuidThreshold)) {
            userUuidThreshold = "10";  //默认阀值
            redisClient.set("userUuidThreshold", userUuidThreshold);  //阀值存redis中，下次要修改阀值的话，直接修改redis中的adUuidThreshold的值
        }
        params.put("adUuidThreshold", Double.parseDouble(adUuidThreshold));
        params.put("userUuidThreshold", Double.parseDouble(userUuidThreshold));

        //广告行为监测结果
        List<MonitorVo> adMonitorList = monitorMapper.monitorAdAction(params);
        String result1 = monitorString(1, adMonitorList);

        //视频用户行为监测结果
        List<MonitorVo> userMonitorList = monitorMapper.monitorUserAction(params);
        String result2 = monitorString(2, userMonitorList);

        //来电用户行为监测结果
        List<MonitorVo> ldUserMonitorList = monitorMapper.monitorLdUserAction(params);
        String result3 = monitorString(3, ldUserMonitorList);

        return result1 + result2 + result3;
    }

    private String monitorString(int type,List<MonitorVo> monitorList) {
        if(monitorList == null || monitorList.isEmpty()) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        if(type == 1) {
            result.append("【广告行为uuid为空预警（广告位ad_key纬度）】\n");
        } else if(type == 2) {
            result.append("【视频用户行为uuid为空预警（页面view纬度）】\n");
        } else if(type == 3) {
            result.append("【来电用户行为uuid为空预警（页面view纬度）】\n");
        }
        monitorList.forEach(r -> {
            result.append(r.getType()+"-" + r.getAppVersion() + "的uuid丢失率为：" + r.getLoseRate() + "%\n");
        });
        return result.toString();
    }
}
