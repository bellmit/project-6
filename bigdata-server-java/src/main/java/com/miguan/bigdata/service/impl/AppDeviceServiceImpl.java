package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.SymbolConstants;
import com.miguan.bigdata.entity.mongo.AppDeviceVo;
import com.miguan.bigdata.entity.xy.ClDevice;
import com.miguan.bigdata.mapper.LdClDeviceMapper;
import com.miguan.bigdata.mapper.XyClDeviceMapper;
import com.miguan.bigdata.service.AppDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class AppDeviceServiceImpl implements AppDeviceService {

    @Resource(name = "idMappingMongoTemplate")
    private MongoTemplate idMappingMongoTemplate;
    @Resource
    private XyClDeviceMapper xyClDeviceMapper;
    @Resource
    private LdClDeviceMapper ldClDeviceMapper;

    @Override
    public List<AppDeviceVo> findDistinctByTime(String beginTime, String endTime, int skip, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("createTime").gte(beginTime).lte(endTime));
        query.with(Sort.by(Sort.Order.asc("createTime")));
        query.skip(skip);
        query.limit(limit);
        return idMappingMongoTemplate.find(query, AppDeviceVo.class, collection_name);
    }

    @Override
    public void update4NpushInit(String packageName, String distinct_id, String actDay, Integer npushState, Integer npushChannel, Integer lastVisitDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("package_name").is(packageName));
        query.addCriteria(Criteria.where("distinct_id").is(distinct_id));
        Update update = new Update();
        update.set("npush_state", npushState);
        update.set("dt", Integer.parseInt(actDay.replaceAll(SymbolConstants.line, "")));
        update.set("npush_channel", npushChannel);
        update.set("last_visit_date", lastVisitDate);
        idMappingMongoTemplate.updateMulti(query, update, collection_name);
    }

    @Override
    public void updateNpushStateToStop(String packageName, String distinct_id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("package_name").is(packageName));
        query.addCriteria(Criteria.where("distinct_id").is(distinct_id));
        Update update = new Update();
        update.set("npush_state", 0);
        idMappingMongoTemplate.updateMulti(query, update, collection_name);
    }

    @Override
    public long countDistinctByDtAndPackageName(Integer dt, String packageName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dt").is(dt));
        query.addCriteria(Criteria.where("package_name").is(packageName));
        query.addCriteria(Criteria.where("npush_state").is(1));
        return idMappingMongoTemplate.count(query, collection_name);
    }

    @Override
    public List<String> findDistinctByDtAndPackageName(Integer dt, String packageName, int skip, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dt").is(dt));
        query.addCriteria(Criteria.where("package_name").is(packageName));
        query.addCriteria(Criteria.where("npush_state").is(1));
        query.with(Sort.by(Sort.Order.asc("createTime")));
        query.skip(skip);
        query.limit(limit);
        //query.fields().include("distinct_id");
        List<AppDeviceVo> deviceList = idMappingMongoTemplate.find(query, AppDeviceVo.class, collection_name);
        if (isEmpty(deviceList)) {
            return null;
        }

        // 如果推送渠道是友盟的设备，需排除最近一次登录日期超过30天的设备
        int thirtyDayAgoDt = Integer.parseInt(LocalDate.now().minusDays(30L).format(DateTimeFormatter.BASIC_ISO_DATE));
        List<String> distinctIdList = deviceList.stream().filter(a -> {
            if (a.getNpush_channel() != null && a.getNpush_channel() == 1) {
                log.debug("设备[{}]推送通道为友盟, 最新登录日期[{}]", a.getDistinct_id(), a.getLast_visit_date());
                if (a.getLast_visit_date() >= thirtyDayAgoDt) {
                    return true;
                } else {
                    log.debug("设备[{}]推送通道为友盟, 最新登录日期超过30日", a.getDistinct_id());
                    return false;
                }
            } else {
                return true;
            }
        }).map(AppDeviceVo::getDistinct_id).collect(Collectors.toList());
        log.info("过滤友盟设备后的推送设备列表>>{}", JSONObject.toJSONString(distinctIdList));
        return distinctIdList;
    }

    @Override
    public void updateDistinctNPushChannel(JSONObject param) {
        // cl_device表的设备ID
        String deviceId = param.getString("deviceId");
        // 推送渠道 -1 无 1 友盟 2 华为 3 vivo 4 oppo 5 小米 int
        int pushChannel = param.getIntValue("pushChannel");
        // 来电 laidian，视频 video
        String appType = param.getString("appType");
        boolean isVideo = StringUtils.equals(appType, "video");

        List<ClDevice> deviceList = null;
        if (isVideo) {
            deviceList = xyClDeviceMapper.selectByDeviceId(deviceId);
        } else {
            deviceList = ldClDeviceMapper.selectByDeviceId(deviceId);
        }

        if (isEmpty(deviceList)) {
            return;
        }

        ClDevice device = deviceList.get(0);
        String packageName = isVideo ? device.getAppPackage() : "com.mg.phonecall";

        Query query = new Query();
        query.addCriteria(Criteria.where("distinct_id").is(device.getDistinctId()));
        query.addCriteria(Criteria.where("package_name").is(packageName));
        Update update = new Update();
        update.set("npush_channel", pushChannel);
        idMappingMongoTemplate.updateMulti(query, update, AppDeviceVo.class, collection_name);
    }
}
