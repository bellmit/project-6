package com.miguan.xuanyuan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.MaterialShapeEnum;
import com.miguan.xuanyuan.common.util.DateUtils;
import com.miguan.xuanyuan.dto.AdCodeDto;
import com.miguan.xuanyuan.dto.CreativeParamsDto;
import com.miguan.xuanyuan.mapper.OriginalityMapper;
import com.miguan.xuanyuan.service.OriginalityService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.CreativeInfoVo;
import com.miguan.xuanyuan.vo.DesignInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tool.util.BeanUtil;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OriginalityServiceImpl implements OriginalityService {
    @Resource
    private OriginalityMapper mapper;

    @Resource
    private RedisService redisService;
    @Override
    public CreativeInfoVo creativeInfo(CreativeParamsDto queueVo) {
        //查询可以使用的创意
        List<DesignInfoVo> designInfoVos = this.getDesignInfoInCache(queueVo);
        if(CollectionUtils.isEmpty(designInfoVos)){
            return null;
        }
        //过滤投放时间
        designInfoVos = designInfoVos.stream().filter(designInfo -> fillPutTime(designInfo)).collect(Collectors.toList());
        DesignInfoVo designInfoVo = chooiseCreative(designInfoVos);
        if(designInfoVo == null){
            return null;
        }
        return fillCreativeInfo(designInfoVo);
    }

    private CreativeInfoVo fillCreativeInfo(DesignInfoVo designInfoVo) {
        CreativeInfoVo creativeInfoVo = new CreativeInfoVo();
        BeanUtil.copyProperties(designInfoVo,creativeInfoVo);
        creativeInfoVo.setShape(MaterialShapeEnum.getValue(designInfoVo.getMaterialShape()));
        String xyLog = redisService.get(RedisKeyConstant.CONFIG_CODE + RedisKeyConstant.XY_LOGO);
        creativeInfoVo.setXyPlatLogo(xyLog);
        return creativeInfoVo;
    }

    private DesignInfoVo chooiseCreative(List<DesignInfoVo> designInfoVos) {
        if(CollectionUtils.isEmpty(designInfoVos)){return null;}
        int weight = 0; //总权重
        List<Integer> weights = Lists.newArrayList();
        for (DesignInfoVo designInfoVo : designInfoVos) {
            weight += designInfoVo.getWeight();
            weights.add(weight);
        }
        if(weight==0)return designInfoVos.get(0);//存在概率都为0的数据
        Random random = new Random();
        int a = random.nextInt(weight) + 1;
        for (int i = 0 ; i < weights.size() ; i++) {
            if(a <= weights.get(i)){
                return designInfoVos.get(i);
            }
        }
        return designInfoVos.get(0);
    }


    private List<DesignInfoVo> getDesignInfoInCache(CreativeParamsDto queueVo) {
        String appKey = queueVo.getAppKey();
        String code = queueVo.getCode();
        Long positionId = queueVo.getPositionId();
        String key = RedisKeyConstant.CREATIVE_INFO_CACHE + "?appKey=" +appKey + "&code=" + code + "&positionId=" + positionId;
        String designInfoList = redisService.get(key);
        List<DesignInfoVo> designInfoList1 = Lists.newArrayList();
        if(StringUtils.isEmpty(designInfoList)){
            designInfoList1 = mapper.findDesignInfos(appKey,code,positionId);
            if(CollectionUtils.isNotEmpty(designInfoList1)){
                redisService.set(key, JSONObject.toJSONString(designInfoList1),RedisKeyConstant.CREATIVE_INFO_CACHE_SECOND);
            }
        } else {
            designInfoList1 = JSONObject.parseArray(designInfoList,DesignInfoVo.class);
        }
        return designInfoList1;
    }

    private boolean fillPutTime(DesignInfoVo designInfo) {

        Date now = new Date();
        if(designInfo.getPutTimeType() != null){
            if(XyConstant.PLAN_PUT_TIME_BETTWEN == designInfo.getPutTimeType()){
                if((designInfo.getStartDate()!=null && designInfo.getStartDate().after(now))
                        || (designInfo.getEndDate() != null && designInfo.getEndDate().before(now))){
                    return false;
                }
            }
        }

        if(designInfo.getTimeSetting() == null){
            return false;
        }
        if(XyConstant.PLAN_TIME_SETTING_ALL == designInfo.getTimeSetting()){
            return true;
        }
        if(XyConstant.PLAN_TIME_SETTING_BETTWEN == designInfo.getTimeSetting()){
            if(StringUtils.isNotEmpty(designInfo.getTimesConfig())){
                String timeConfig = designInfo.getTimesConfig();
                String start = timeConfig.split("-")[0];
                String end = timeConfig.split("-")[1];
                return isTimeRange(start,end);
            }
            return false;
        }
        if(XyConstant.PLAN_TIME_SETTING_CONFIG == designInfo.getTimeSetting()){
            if(StringUtils.isNotEmpty(designInfo.getTimesConfig())){
                List<Map<String,Object>> timeConfLst = JSONObject.parseObject(designInfo.getTimesConfig(), List.class);
                boolean tcFlag = false;
                for (int i = 0; i < timeConfLst.size(); i++) {
                    if(tcFlag){
                        break;
                    }
                    JSONObject jsonObject = (JSONObject) timeConfLst.get(i);
                    Integer wkDay = (Integer) jsonObject.get("week_day");
                    JSONArray timeDurs = (JSONArray)jsonObject.get("time");
                    for (int j = 0; j < timeDurs.size(); j++) {
                        String weekDay = DateUtils.dayForWeek(now);
                        String timeDur = (String) timeDurs.get(j);
                        String start = timeDur.split("-")[0];
                        String end = timeDur.split("-")[1];
                        //天数一致，时间点在范围内
                        if (StringUtils.isNotEmpty(weekDay) && Integer.parseInt(weekDay) == wkDay
                                && isTimeRange(start,end)){
                            tcFlag = true;
                            break;
                        }
                    }
                }
                //如果不包含在时间配置里面，则过滤
                if(!tcFlag){
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断广告计划是否投放中
     *
     * @param putTimeType
     * @param startDate
     * @param endDate
     * @param timeSetting
     * @param timesConfig
     * @return
     */
    public boolean isPlanActive(Integer putTimeType, Date startDate, Date endDate, Integer timeSetting,  String timesConfig) {
        Date now = new Date();
        if(putTimeType != null){
            if(XyConstant.PLAN_PUT_TIME_BETTWEN == putTimeType){
                if((startDate !=null && startDate.after(now))
                        || (endDate != null && endDate.before(now))){
                    return false;
                }
            }
        }

        if(timeSetting == null){
            return false;
        }
        if(XyConstant.PLAN_TIME_SETTING_ALL == timeSetting){
            return true;
        }
        if(XyConstant.PLAN_TIME_SETTING_BETTWEN == timeSetting){
            if(StringUtils.isNotEmpty(timesConfig)){
                String timeConfig = timesConfig;
                String start = timeConfig.split("-")[0];
                String end = timeConfig.split("-")[1];
                return isTimeRange(start,end);
            }
            return false;
        }
        if(XyConstant.PLAN_TIME_SETTING_CONFIG == timeSetting){
            if(StringUtils.isNotEmpty(timesConfig)){
                List<Map<String,Object>> timeConfLst = JSONObject.parseObject(timesConfig, List.class);
                boolean tcFlag = false;
                for (int i = 0; i < timeConfLst.size(); i++) {
                    if(tcFlag){
                        break;
                    }
                    JSONObject jsonObject = (JSONObject) timeConfLst.get(i);
                    Integer wkDay = (Integer) jsonObject.get("week_day");
                    JSONArray timeDurs = (JSONArray)jsonObject.get("time");
                    for (int j = 0; j < timeDurs.size(); j++) {
                        String weekDay = DateUtils.dayForWeek(now);
                        String timeDur = (String) timeDurs.get(j);
                        String start = timeDur.split("-")[0];
                        String end = timeDur.split("-")[1];
                        //天数一致，时间点在范围内
                        if (StringUtils.isNotEmpty(weekDay) && Integer.parseInt(weekDay) == wkDay
                                && isTimeRange(start,end)){
                            tcFlag = true;
                            break;
                        }
                    }
                }
                //如果不包含在时间配置里面，则过滤
                if(!tcFlag){
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否在时间区间内
     * @param startH
     * @param endH
     * @return
     * @throws ParseException
     */
    private boolean isTimeRange(String startH, String endH) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date now = df.parse(df.format(new Date()));
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(now);

            Date begin = df.parse(startH);
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(begin);

            Date end = df.parse(endH);
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(end);
            if (nowTime.before(endTime) && nowTime.after(beginTime)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
