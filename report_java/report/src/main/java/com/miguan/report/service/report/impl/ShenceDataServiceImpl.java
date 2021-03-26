package com.miguan.report.service.report.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.report.common.constant.ShenceConstant;
import com.miguan.report.common.enums.ShenceAppKeyEnum;
import com.miguan.report.entity.report.ShenceData;
import com.miguan.report.repository.ShenceDataRepository;
import com.miguan.report.service.report.ShenceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 神策service
 */
@Slf4j
@Configuration
@Service
public class ShenceDataServiceImpl implements ShenceDataService {

    @Value("${shence.videoUrl}")
    private String videoUrl;
    @Value("${shence.laidianUrl}")
    private String laidianUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ShenceDataRepository shenceDataRepository;

    private String newUsersParam = "{'measures':[{'event_name':'$AppStart','aggregator':'unique','filter':{'conditions':[{'field':'event.$AppStart.$is_first_day','function':'isTrue','params':[]}]}}],'unit':'day','filter':{},'by_fields':['event.$AppStart.package_name'],'sampling_factor':64,'axis_config':{'isNormalize':false,'left':[],'right':[]},'from_date':'{date}','to_date':'{date}','approx':false,'detail_and_rollup':true,'layer_other_rollup':false,'isSaved':false,'sub_task_type':'SEGMENTATION','jump_url':'/segmentation/?project={project}#measures%5B0%5D%5Bevent_name%5D=%24AppStart&measures%5B0%5D%5Baggregator%5D=unique&measures%5B0%5D%5Bfilter%5D%5Bconditions%5D%5B0%5D%5Bfield%5D=event.%24AppStart.%24is_first_day&measures%5B0%5D%5Bfilter%5D%5Bconditions%5D%5B0%5D%5Bfunction%5D=isTrue&unit=day&by_fields%5B%5D=event.%24AppStart.package_name&chartsType=line&sampling_factor=64&axis_config%5BisNormalize%5D=false&rangeText=&from_date={date}&to_date={date}&approx=false&request_id=1593408407499:776410','enable_detail_follow_rollup_by_values_rank':true,'request_id':'1593408407499:776410','use_cache':true}";
    private String totalUsersParam = "{'measures':[{'event_name':'$AppStart','aggregator':'unique'}],'unit':'day','filter':{},'by_fields':['event.$AppStart.package_name'],'sampling_factor':64,'axis_config':{'isNormalize':false,'left':[],'right':[]},'from_date':'{date}','to_date':'{date}','approx':false,'detail_and_rollup':true,'layer_other_rollup':false,'isSaved':false,'sub_task_type':'SEGMENTATION','jump_url':'/segmentation/?project={project}%5D=%24AppStart&measures%5B0%5D%5Baggregator%5D=unique&unit=day&by_fields%5B%5D=event.%24AppStart.package_name&chartsType=line&sampling_factor=64&axis_config%5BisNormalize%5D=false&rangeText=&from_date={date}&to_date={date}&approx=false&request_id=1593412539172:692445','enable_detail_follow_rollup_by_values_rank':true,'request_id':'1593412539172:692445','use_cache':true}";
    /**
     * 日活
     */
    private String activeVideoParam = "{'measures':[{'event_name':'$AppStart','aggregator':'unique'}],'unit':'day','filter':{},'by_fields':['event.$AppStart.package_name'],'sampling_factor':64,'axis_config':{'isNormalize':false,'left':[],'right':[]},'from_date':'{date}','to_date':'{date}','approx':false,'detail_and_rollup':true,'layer_other_rollup':false,'isSaved':false,'sub_task_type':'SEGMENTATION','jump_url':'/segmentation/?project=xiyoushipin#measures%5B0%5D%5Bevent_name%5D=%24AppStart&measures%5B0%5D%5Baggregator%5D=unique&unit=day&by_fields%5B%5D=event.%24AppStart.package_name&chartsType=line&sampling_factor=64&axis_config%5BisNormalize%5D=false&rangeText=&from_date={date}&to_date={date}&approx=false&request_id=1587610303045:520658','request_id':'1587610303045:520658','use_cache':true}";
    private String activeCallParam = "{'measures':[{'event_name':'$AppStart','aggregator':'unique'}],'unit':'day','filter':{},'by_fields':[],'sampling_factor':64,'axis_config':{'isNormalize':false,'left':[],'right':[]},'from_date':'{date}','to_date':'{date}','approx':false,'detail_and_rollup':true,'layer_other_rollup':false,'isSaved':false,'sub_task_type':'SEGMENTATION','jump_url':'/segmentation/?project=xuanlaidian#measures%5B0%5D%5Bevent_name%5D=%24AppStart&measures%5B0%5D%5Baggregator%5D=unique&unit=day&chartsType=line&sampling_factor=64&axis_config%5BisNormalize%5D=false&rangeText=&from_date={date}&to_date={date}&approx=false&request_id=1588935987511:815121','request_id':'1588935987511:815121','use_cache':true}";

    /**
     * 获取每日新用户数
     *
     * @param date    日期：yyyy-MM-dd
     * @param appType app类型，1：视频，2：来电
     * @param type 类型：1-新增用户数，2-总用户数 3-日活
     */
    private Map<String, Integer> getData(String date, int appType, int type) {
        try {
            String param = "";
            switch (type) {
                case ShenceConstant.TYPE_NEW_USERS: {
                    param = newUsersParam;
                    break;
                }
                case ShenceConstant.TYPE_TOTAL_USERS: {
                    param = totalUsersParam;
                    break;
                }

                case ShenceConstant.TYPE_ACTIVE_USERS: {
                    param = appType == 1 ? activeVideoParam : activeCallParam;
                    break;
                }
                default: {
                    param = newUsersParam;
                    break;
                }
            }
            param = param.replace("{date}", date);
            param = (appType == 1 ? param.replace("{project}", "xiyoushipin") : param.replace("{project}", "xuanlaidian"));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(param, headers);
            String url = (appType == 1 ? videoUrl : laidianUrl);
            String newUsersJsonStr = restTemplate.postForObject(url, request, String.class);
            JSONArray newUsersArray = JSONObject.parseObject(newUsersJsonStr).getJSONObject("detail_result").getJSONArray("rows");
            Map<String, Integer> result = new HashMap<>();
            for (int i = 0; i < newUsersArray.size(); i++) {
                try {
                    JSONObject newUser = newUsersArray.getJSONObject(i);
                    String valueStr = newUser.getJSONArray("values").getString(0);
                    int value = Integer.parseInt(valueStr.replace("[", "").replace("]", ""));
                    result.put(newUser.getJSONArray("by_values").getString(0), value);
                } catch (Exception e) {
                    log.error("获取神策数据错误有空");
                }
            }
            return result;
        } catch (Exception e) {
            log.error("获取神策数据错误{}", date, e);
            return new HashMap<>();
        }
    }

    /**
     * 保存神策视频当天的数据
     * @param date 日期：yyyy-MM-dd
     * @param appType app类型
     */
    @Override
    public void saveShenceData(String date, int appType) {
        Map<String, Integer> newUsers = getData(date, appType, ShenceConstant.TYPE_NEW_USERS);  //视频新增用户数
        Map<String, Integer> totalUsers = getData(date, appType, ShenceConstant.TYPE_TOTAL_USERS);  //视频新增用户数
        Map<String, Integer> activeUsers = getData(date, appType, ShenceConstant.TYPE_ACTIVE_USERS);  //视频新增用户数

        this.deleteShenceData(date, date, appType);
        for (ShenceAppKeyEnum appKey : ShenceAppKeyEnum.values()) {
            if (appKey.getAppType().intValue() == appType) {
                ShenceData shenceData = new ShenceData(date, appKey.getAppType(), appKey.getId(), appKey.getName(), appKey.getClientId());
                shenceData.setNewUsers(newUsers.get(appKey.getCode()) == null ? 0 : newUsers.get(appKey.getCode()));
                shenceData.setTotalUsers(totalUsers.get(appKey.getCode()) == null ? 0 : totalUsers.get(appKey.getCode()));
                shenceData.setActive(activeUsers.get(appKey.getCode()) == null ? 0 : activeUsers.get(appKey.getCode()));
                shenceData.setCreatedAt(new Date());
                shenceDataRepository.save(shenceData);
            }
        }
    }

    /**
     * 删除友盟的数据
     * @param startDate
     * @param endDate
     */
    @Override
    public void deleteShenceData(String startDate, String endDate, int appType) {
        shenceDataRepository.deleteByDate(startDate, endDate, appType);
    }
}
