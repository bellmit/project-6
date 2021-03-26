package com.miguan.ballvideo.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.entity.dsp.Page;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class TimeConfigFormat {

    /**
     * 根据PHP写的逻辑。为了保证跟以前的存储格式一致，避免一些未知的问题 , 修改的地方,原有的PHP有个BUG，会忽略掉30分钟。
     * @param timeSetting
     * @param timesConfig
     */
    public static String analysisTimeConfig(Integer timeSetting,String timesConfig) throws ValidateException {
        if(TypeConstant.TIME_SETTING_APPOINT == timeSetting){
            return timesConfig;
        } else if (TypeConstant.TIME_SETTING_APPOINT_MANY == timeSetting){
            //这算是校验没过关了 防止垃圾数据
            if(StringUtils.isEmpty(timesConfig)){
                throw new ValidateException("您所选的投放时间类型为指定多个时段,但是却未选择时间段.");
            }
            JSONArray timeInfos = JSON.parseArray(timesConfig);
            JSONArray newTimeInfos = new JSONArray();
            for (int i = 0 ; i < timeInfos.size() ; i++){
                JSONObject timeInfo = timeInfos.getJSONObject(i);
                if(timeInfo == null || StringUtils.isEmpty(timeInfo.getString("arr"))){
                    continue;
                }
                JSONArray arr = timeInfo.getJSONArray("arr");
                JSONObject newTimeInfo = new JSONObject();
                newTimeInfo.put("week_day",i + 1);
                List<String> times = Lists.newArrayList();
                arr.forEach(obj -> {
                    List<Integer> selectTime = (List<Integer>)obj;
                    if(!CollectionUtils.isEmpty(selectTime)){
                        String startTime = "";
                        String endTime = "";
                        if(selectTime.get(0) % 2 == 0){
                            startTime = selectTime.get(0) / 2 + ":00";
                        } else {
                            startTime = selectTime.get(0) / 2 + ":30";
                        }
                        if(selectTime.get(selectTime.size() - 1) % 2 == 0){
                            endTime = (selectTime.get(selectTime.size() - 1) / 2) + ":30";
                        } else {
                            endTime = ((selectTime.get(selectTime.size() - 1) / 2) + 1 ) + ":00";
                        }
                        times.add(startTime+"-"+endTime);
                    }
                });
                newTimeInfo.put("time",times);
                newTimeInfos.add(newTimeInfo);
            }
            if(newTimeInfos.isEmpty()){
                return timesConfig;
            } else {
                return newTimeInfos.toJSONString();
            }
        } else {
            return timesConfig;
        }
    }


    /**
     * 根据PHP写的逻辑。为了保证跟以前的存储格式一致，避免一些未知的问题 , 修改的地方,原有的PHP有个BUG，会忽略掉30分钟。
     * @param timeSetting
     * @param timesConfig
     */
    public static Object reanalysisTimeConfig(Integer timeSetting, String timesConfig) {
        if(TypeConstant.TIME_SETTING_APPOINT == timeSetting){
            return timesConfig;
        } else if (TypeConstant.TIME_SETTING_APPOINT_MANY == timeSetting){
            //这算是校验没过关了 防止垃圾数据
            if(StringUtils.isEmpty(timesConfig)){
                return timesConfig;
            }
            //[{"week_day":1,"time":["3:30-11:00"]},{"week_day":2,"time":["3:30-11:00"]},{"week_day":4,"time":["8:00-13:00"]}]
            JSONArray timeInfos = JSON.parseArray(timesConfig);
            List<Map<String,List<Integer>>> result = initResult();

            for (int i = 0 ; i < timeInfos.size() ; i++){
                JSONObject timeInfo = timeInfos.getJSONObject(i);
                Integer key = timeInfo.getInteger("week_day") - 1;
                JSONArray times = timeInfo.getJSONArray("time");
                if(times == null){
                    continue;
                }
                List<Integer> timeNum = Lists.newArrayList();
                times.forEach(time -> {
                    int startHour = 0;
                    int endHour = 0;
                    String timeStr = (String)time;
                    String[] timeSplit = timeStr.split("-");
//                    String start_hour_arr = timeSplit[0];
//                    String end_hour_arr = timeSplit[1];
                    //处理开始时间
                    String[] startHourArr = timeSplit[0].split(":");
                    if("30".equals(startHourArr[1])){
                        startHour = (Integer.valueOf(startHourArr[0]) * 2)+1;
                    } else {
                        startHour = (Integer.valueOf(startHourArr[0]) * 2);
                    }
                    //处理结束时间
                    String[] endHourArr = timeSplit[1].split(":");
                    if("30".equals(endHourArr[1])){
                        endHour = (Integer.valueOf(endHourArr[0]) * 2);
                    } else {
                        endHour = (Integer.valueOf(endHourArr[0]) * 2) - 1;
                    }
                    for( int k = startHour ; k <= endHour ; k++){
                        timeNum.add(k);
                    }
                });
                Map<String,List<Integer>> arrNum = Maps.newHashMap();
                arrNum.put("arr",timeNum);
                result.set(key,arrNum);
            }
            return result;
        } else {
            return timesConfig;
        }
    }

    private static List<Map<String, List<Integer>>> initResult() {
        List<Map<String, List<Integer>>> result= Lists.newArrayList();
        for (int i = 0 ; i < 7 ; i ++){
            Map<String, List<Integer>> init = Maps.newHashMap();
            init.put("arr",Lists.newArrayList());
            result.add(init);
        }
        return result;
    }
}
