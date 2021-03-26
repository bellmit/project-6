package com.miguan.ballvideo.service.dsp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.dsp.DspConstant;
import com.miguan.ballvideo.common.util.dsp.DspGlobal;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.mapper3.BudgetSmoothMapper;
import com.miguan.ballvideo.mapper3.ReportMapper;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.dsp.ReportService;
import com.miguan.ballvideo.service.dsp.nadmin.BudgetSmoothService;
import com.miguan.ballvideo.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tool.util.BigDecimalUtil;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.ballvideo.common.util.NumCalculationUtil.roundHalfUpDouble;

/**
 * 预算平滑serviceImpl
 */
@Slf4j
@Service
public class BudgetSmoothServiceImpl implements BudgetSmoothService {

    @Resource
    private BudgetSmoothMapper budgetSmoothMapper;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisService redisService;

    //统计近7天每个时间段的日活数占比 接口url
    @Value("${bigdata-server.get-user-ratio}")
    private String userRatioUrl;

    /**
     * 预算平滑算法、参竞率（根据每个时间段的日活数，计算出每个时间段的预算）
     */
    public void budgetSmooth() {
        String date = DateUtil.dateStr7(new Date());  //日期
        Map<String, Object> userRatio = getUserRatio();  //近7天每个时间段的日活数占比
        //统计前，先删除之前统计存入redis的数据,防止在统计期间，预算还在根据就的预算在扣费
        List<Integer> planIds = budgetSmoothMapper.findBugeSmoothPlanIds();
        planIds.forEach(r -> {redisService.del("timeSlotPrice:" + date + ":" + r);});

        //开始计算每个计划在当天每个时间段的预算
        List<BudgetAccountVo> budgetPlans = budgetSmoothMapper.findBugeSmoothPlan();  //查询出需要做预算平滑的计划列表
        for(BudgetAccountVo accountVo : budgetPlans) {
            double remainDayPrice = accountVo.getRemainDayPrice();
            //根据投放时间段类型，计算出每个时间段的预算
            LinkedHashMap<String, Double> planBudget = computePlanBudget(userRatio, remainDayPrice, accountVo.getTimeSetting(), accountVo.getTimesConfig());

            String redisKey = "timeSlotPrice:" + date + ":" + accountVo.getPlanId();
            //计算出来的每个时间段的预算，存入redis
            redisService.set(redisKey, JSON.toJSONString(planBudget), 24 * 60 * 60);
            log.info("预算平滑时间段预算,key:{},value:{}", redisKey, JSON.toJSONString(planBudget));

            Double currentBudget = planBudget.get(currentTimeSlot());  //当前时间段的预算
            if(currentBudget != null) {
                boolean isMinTimeSlot = this.isMinTimeSlot(accountVo.getTimeSetting(), accountVo.getTimesConfig());  //当前时间是否是当天最小的时间段
                computeParticipationRate(currentBudget, accountVo.getPlanId(), isMinTimeSlot); //计算当前时间计划的参竞率
            }

            //把当前时间的预算存入redis，在下个时间段计算参竞率时使用
            if(currentBudget != null) {
                redisService.set("lastPlanBudget:" + accountVo.getPlanId(), currentBudget, 24 * 60 * 60);
            }
        }
    }

    /**
     *
     * @param userRatio  //近7天每个时间段的日活数占比
     * @param remainDayPrice  //剩余日预算
     * @param timeSetting  投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段
     * @param timesConfig  时间配置。指定时间段的给是为：10:45-11:80;多个时段的json格式：[{week_day:1,start_hour:0,end_hour:47}]
     * @return
     */
    private LinkedHashMap<String, Double> computePlanBudget(Map<String, Object> userRatio, double remainDayPrice, int timeSetting, String timesConfig) {
        LinkedHashMap<String, Double> planBudget = new LinkedHashMap<>();
        if(timeSetting == 0) {
            //投放时间段：全天
            userRatio.forEach((key, value) -> {
                double rate = Double.parseDouble(String.valueOf(value));
                double timeSlotPrice = BigDecimalUtil.mul(remainDayPrice, rate);  //时间段的预算= 日剩余预算 * 时间段的日活占比
                planBudget.put(key, timeSlotPrice);
            });
        } else if(timeSetting == 1) {
            //时间段内的活跃用户占比求和
            double totalRat = userRatio.entrySet().stream().filter(m ->this.isInTimeSection(timesConfig,m.getKey()))
                    .mapToDouble(m -> Double.parseDouble(String.valueOf(m.getValue()))).sum();

            userRatio.forEach((key, value) -> {
                if(this.isInTimeSection(timesConfig, key)) {
                    double rate = Double.parseDouble(String.valueOf(value));
                    rate = (totalRat == 0 ? 0 : rate/totalRat);  //重新计算时间段日活占比
                    double timeSlotPrice = BigDecimalUtil.mul(remainDayPrice, rate);  //时间段的预算= 日剩余预算 * 时间段的日活占比
                    planBudget.put(key, timeSlotPrice);
                }
            });
        } else if(timeSetting == 2) {
            //多个时间段内的活跃用户占比求和
            double totalRat = userRatio.entrySet().stream().filter(m ->this.isInMultipleTime(timesConfig,m.getKey()))
                    .mapToDouble(m -> Double.parseDouble(String.valueOf(m.getValue()))).sum();

            userRatio.forEach((key, value) -> {
                if(this.isInMultipleTime(timesConfig, key)) {
                    double rate = Double.parseDouble(String.valueOf(value));
                    rate = (totalRat == 0 ? 0 : rate/totalRat);  //重新计算时间段日活占比
                    double timeSlotPrice = BigDecimalUtil.mul(remainDayPrice, rate);  //时间段的预算= 日剩余预算 * 时间段的日活占比
                    planBudget.put(key, timeSlotPrice);
                }
            });
        }
        return planBudget;
    }

    /**
     * 计算当前时间计划的参竞率,第一个时间片的参竞率默认为0.1
     * 当前时间片的参竞率P2=P1*R2/R1 （P1上个时间片的参竞率、R2当前时间片的计划消耗、R1上个时间片的计划消耗）
     *
     * @param r2 当前时间片的计划消耗
     * @param planId 计划id
     * @param isMinTimeSlot 当前时间是否是当天最小的时间段
     */
    private void computeParticipationRate(Double r2, Integer planId, boolean isMinTimeSlot) {
        String partRatekey = "partRate:" + planId;   //参竞率的key
        String lastPlanBudget = "lastPlanBudget:" + planId;  //上个时间片的预算

        if(isMinTimeSlot) {
            //第一个时间片的参竞率默认为0.1,
            redisService.set(partRatekey, DspGlobal.getValue(DspConstant.INIT_PART_RATE), 24 * 60 * 60);
        } else {
            Double r1 = redisService.getDouble(lastPlanBudget) == null ? r2 : redisService.getDouble(lastPlanBudget);  //查询出上个时间片的计划消耗
            Double p1 = redisService.getDouble(partRatekey) == null ? DspGlobal.getDouble(DspConstant.INIT_PART_RATE) : redisService.getDouble(partRatekey);  //查询出上个时间片的参竞率
            log.info("前时间片的计划消耗：{},上个时间片的计划消耗：{},上个时间片的参竞率：{}", r2, r1, p1);

            //计算出当前时间片的参竞率
            double p2 = roundHalfUpDouble(6, p1*r2/r1);
            p2 = (p2==0 ? DspGlobal.getDouble(DspConstant.INIT_PART_RATE) : p2);
            redisService.set(partRatekey, p2, 24 * 60 * 60);
            log.info("当前时间片的参竞率：{}", p2);
        }
    }

    /**
     * 当前时间段的预算，是否还有剩余
     * @param planId 计划id
     * @param price 单价
     * @return true:还有剩余,false：已无预算
     */
    public boolean isHasBudget(Integer planId, double price) {
        String date = DateUtil.dateStr7(new Date());  //日期
        String key = "timeSlotPrice:" + date + ":" + planId;
        String value = redisService.get(key);   //从redis中拿出预算平滑，没个时间段的预算
        if(StringUtils.isBlank(value)) {
            return false;
        }
        Map<String, Object> planBudget = JSONObject.parseObject(value, Map.class);
        String timeSlot = currentTimeSlot();  //获取当前时间的实际段
        String timeSlotPriceStr = planBudget.get(currentTimeSlot()) == null ? "0" : String.valueOf(planBudget.get(currentTimeSlot()));
        Double timeSlotPrice = Double.parseDouble(timeSlotPriceStr); //获取当前时间段内的预算

        log.info("时间段的预算为{}：{}", timeSlot, timeSlotPrice);
        if(timeSlotPrice < price) {
            //预算如果比单价小，则预算不足
            return false;
        }
        return true;
    }

    /**
     * 广告点击后，减少对应时间段的预算值
     * @param planId 计划id
     * @param price 单价
     */
    public void reduceTimeSlotPrice(Integer planId, double price) {
        String date = DateUtil.dateStr7(new Date());  //日期
        String key = "timeSlotPrice:" + date + ":" + planId;
        String value = redisService.get(key);   //从redis中拿出预算平滑，没个时间段的预算
        if(StringUtils.isBlank(value)) {
            return;
        }
        Map<String, Double> planBudget = JSONObject.parseObject(value, Map.class);
        String timeSlot = currentTimeSlot();  //获取当前时间的实际段
        String timeSlotPriceStr = planBudget.get(currentTimeSlot()) == null ? "0" : String.valueOf(planBudget.get(currentTimeSlot()));
        Double timeSlotPrice = Double.parseDouble(timeSlotPriceStr); //获取时间段内的预算
        if(timeSlotPrice < price) {
            //预算如果比单价小
            return;
        }
        timeSlotPrice = BigDecimalUtil.sub(timeSlotPrice, price);  //预算减少
        planBudget.put(timeSlot, timeSlotPrice);

        redisService.set(key, JSON.toJSONString(planBudget), 24 * 60 * 60); //重新保存到redis
    }

    /**
     * 修改计划组剩余预算
     * @param remainGroupPrice  组剩余预算
     * @param planId 计划id
     */
    public void updateGroupRemainAccount(Double remainGroupPrice, Integer planId) {
        Map<String, Object> params = new HashMap<>();
        params.put("remainGroupPrice", remainGroupPrice);
        params.put("planId", planId);
        budgetSmoothMapper.updateGroupRemainAccount(params);
    }

    /**
     * 判断计划是否命中参竞率
     * @param planId 计划id
     * @return 返回格式：命中结果-参竞率，例如 true-0.15664
     */
    public String isHitPartRate(Integer planId) {
        Double partRate = redisService.getDouble("partRate:" + planId); //计划当前参竞率
        if(partRate == null) {
            return "false-0";
        }
        //获取随机数后，随机判断计划有没命中参竞率
        double random = Math.random();  //获取0~1的随机数
        if(partRate > random) {
            return "true-" + partRate;
        } else {
            return "false-" + partRate;
        }
    }

    /**
     * 获取近7天每个时间段的日活数占比
     */
    private Map<String, Object> getUserRatio() {
        String url = userRatioUrl;
        String resultJson = restTemplate.postForObject(url, null, String.class);  //获取小于阀值的代码位
        log.info("获取近7天每个时间段的日活数占比:{}", resultJson);
        LinkedHashMap<String, Object> map = JSONObject.parseObject(resultJson, LinkedHashMap.class);

        return map;
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

    /**
     * 根据具体时间，获取对应的时间段
     * @param time 具体时间，格式为11:52
     * @return
     */
    private String getTimeSlot(String time) {
        String[] timeArr = time.split(":");
        String hour = timeArr[0] + ":";  //小时
        int minute = Integer.parseInt(timeArr[1]);
        int slot = minute / 30;
        return (slot == 0 ? hour + "00" : hour + "30");
    }

    /**
     * 判断时间段是否在时间区间中
     * @param timeSection 时间区间
     * @param timeSlot 时间段
     * @return
     */
    private boolean isInTimeSection(String timeSection, String timeSlot) {
        String[] timeSections = timeSection.split("-");
        String oneTime = getTimeSlot(timeSections[0]);
        String twoTime = getTimeSlot(timeSections[1]);
        oneTime = (oneTime.length() == 5 ? oneTime : "0" + oneTime);
        twoTime = (twoTime.length() == 5 ? twoTime : "0" + oneTime);

        int startTimeSlot = Integer.parseInt("1" + oneTime.replace(":", ""));  //开始时间段
        int endTimeSlot = Integer.parseInt("1" + twoTime.replace(":", ""));  //结束时间段
        int timeSlotInt = Integer.parseInt("1" + timeSlot.replace(":", ""));   //时间段
        return (timeSlotInt >= startTimeSlot && timeSlotInt < endTimeSlot);
    }

    /**
     * 判断时间段是否在指定多个时间段中
     * @param timeSection 多个时间段json
     * @param timeSlot 时间段
     * @return
     */
    private boolean isInMultipleTime(String timeSection, String timeSlot) {
        JSONArray times = analysisTimesConfig(timeSection);

        //判断时间段是否在用户指定的多个时间段中
        for(int i=0;i<times.size();i++) {
            String time = times.getString(i);
            if(isInTimeSection(time, timeSlot)) {
                return true;
            }
        }
        return false;
    }

    private JSONArray analysisTimesConfig(String timesConfig) {
        JSONArray jsonArray = JSONArray.parseArray(timesConfig);
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        week = (week == 0 ? 7 : week);  //当前星期

        JSONArray times = new JSONArray();
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            if(json.getInteger("week_day") == week) {
                times = json.getJSONArray("time");
                break;
            }
        }
        return times;
    }

    /**
     * 当前时间是否是当天最小的时间段
     * @param timeSetting timeSetting  投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段
     * @param timesConfig 时间配置
     * @return
     */
    public boolean isMinTimeSlot(int timeSetting, String timesConfig) {
        String timeSlot = currentTimeSlot();  //当前时间段
        if(timeSetting == 0) {
            //全天
            return "00:00".equals(timeSlot);
        } else if(timeSetting == 1) {
            //指定开始时间和结束时间
            String[] times = timesConfig.split("-");
            String minTimeSlot = getTimeSlot(times[0]);
            if(minTimeSlot.equals(timeSlot)) {
                return true;
            }
        } else if(timeSetting == 2) {
            //指定多个时段
            JSONArray timeArray = analysisTimesConfig(timesConfig);
            if(timeArray.size() > 1) {
                String timesStr = timeArray.getString(0);
                String[] times = timesStr.split("-");
                String minTimeSlot = getTimeSlot(times[0]);
                if(minTimeSlot.equals(timeSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        map.put("10:30",55D);
        map.put("12:30",55D);
        map.put("01:00",55D);
        map.put("00:30",55D);
        int a = map.entrySet().stream().mapToInt(m->Integer.parseInt("1"+m.getKey().replace(":", ""))).min().getAsInt();
        System.out.println(a);
    }

    /**
     * 98度昨天有效点击率
     * @return
     */
    public double staYesPreClickRate() {
        String yesPreClickRate = redisService.get("yesPreClickRate");
        if(StringUtils.isNotBlank(yesPreClickRate)) {
            return Double.parseDouble(yesPreClickRate);
        } else {
            double yesPreClickRateD = budgetSmoothMapper.staYesPreClickRate();
            redisService.set("yesPreClickRate", String.valueOf(yesPreClickRateD), getTodayEndSecond());
            return yesPreClickRateD;
        }
    }

    /**
     * 获取当前时间 至 当天23:59:59 的秒数
     * @return
     */
    private int getTodayEndSecond() {
        String today = DateUtil.dateStr2(new Date());  //当天
        today = today + " 23:59:59";
        Date todayEnd = DateUtil.valueOf(today, "yyyy-MM-dd HH:mm:ss");
        Long second = (todayEnd.getTime() - System.currentTimeMillis()) / 1000;
        return second.intValue();
    }
}
