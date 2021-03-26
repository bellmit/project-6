package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.dto.EarlyWarningDto;
import com.miguan.bigdata.entity.mongo.AppsInfo;
import com.miguan.bigdata.mapper.AdDataMapper;
import com.miguan.bigdata.mapper.AppListMapper;
import com.miguan.bigdata.mapper.DspPlanMapper;
import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.service.AppListService;
import com.miguan.bigdata.vo.DspPlanVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.bigdata.common.constant.BigDataConstants.warnTypeHour;
import static com.miguan.bigdata.common.util.NumCalculationUtil.roundHalfUpDouble;
import static tool.util.DateUtil.dateStr7;

/**
 * 用户设备应用安装列表
 */
@Slf4j
@Service
public class AppListServiceImpl implements AppListService {

    @Resource
    private AppListMapper appListMapper;

    @Resource(name="driveMongoTemplate")
    private MongoTemplate mongoTemplate;


    /**
     * mongodb的drive库的apps_info_list表中历史数据的distinctId字段数据刷进去
     */
    public void brushOldAppListDistinctId() {
        int startRow = 0;
        int pageSize = 3000;  //每次分页数
        while(true) {
            log.info("开始刷apps_info_list，startRow:{},pageSize:{}", startRow, pageSize);
            List<AppsInfo> appsInfoList = findAppsInfoList(startRow, pageSize);  //从mongodb查询出需要更新distinctId的记录
            if(appsInfoList == null || appsInfoList.isEmpty()) {
                break;
            }

            List<AppsInfo> distinctList = appListMapper.findDistinctIdByDeviceId(appsInfoList);  //查询出deviceId和包名对应的distinctId
            if(distinctList != null && !distinctList.isEmpty()) {
                for(AppsInfo one : distinctList) {
                    updateDistinctId(one.getAppPackage(), one.getDeviceId(), one.getDistinctId());
                }
            }
            startRow+=pageSize;
        }
    }


    /**
     * 分页查询apps_info_list的数据
     * @param startRow  分页开始行数
     * @param pageSize  每次分页查询的记录
     * @return
     */
    private List<AppsInfo> findAppsInfoList(int startRow, int pageSize) {
        Query query=new Query();
        query.skip(startRow);
        query.limit(pageSize);
        List<AppsInfo> list = mongoTemplate.find(query, AppsInfo.class, "apps_info_list");
        return list;
    }

    private void updateDistinctId(String appPackage, String deviceId, String distinctId) {
        if(StringUtils.isBlank(deviceId) || StringUtils.isBlank(distinctId) ) {
            return;
        }
        Query query=new Query();
        query.addCriteria(Criteria.where("appPackage").is(appPackage));
        query.addCriteria(Criteria.where("deviceId").is(deviceId));
        Update update= new Update().set("distinctId", distinctId);
        mongoTemplate.updateMulti(query, update, "apps_info_list");
    }
}
