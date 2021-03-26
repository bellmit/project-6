package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.util.DateUtil;
import com.miguan.bigdata.dto.DeviceInfoDto;
import com.miguan.bigdata.entity.mongo.DeviceInfo;
import com.miguan.bigdata.service.DeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    @Resource(name = "idMappingMongoTemplate")
    private MongoTemplate idMappingMongoTemplate;

    @Override
    public DeviceInfoDto findDistinctIdByChannel(String packageName, Integer actDay, List<String> excludeChannels, String beginDateTime, String endDateTime, Integer limit) {
        log.debug("NpushIter 应用{} - {}日激活的用户分页查询条件: excludeChannels>>{}, beginDateTime>>{}, endDateTime>>{}, limit>>{}", packageName, actDay, excludeChannels, beginDateTime, endDateTime, limit);
        Query query = new Query();
        query.addCriteria(Criteria.where("createTime").gt(beginDateTime).lte(endDateTime));
//        query.addCriteria(Criteria.where("createTime").lte(endDateTime));
        query.addCriteria(Criteria.where("npushState").is(1));
        if (!isEmpty(excludeChannels)) {
            query.addCriteria(Criteria.where("channel").nin(excludeChannels));
        }
        query.with(Sort.by(Sort.Order.asc("createTime")));
        query.limit(limit);
        query.fields().include("distinct_id").include("createTime").exclude("_id");
        List<DeviceInfo> list = idMappingMongoTemplate.find(query, DeviceInfo.class, "android_device_info");
        if(isEmpty(list)){
            log.info("NpushIter 应用{} - {}日激活的用户分页查询结果: beginDateTime>>{}, endDateTime>>{}, 共0人" , packageName, actDay, beginDateTime, endDateTime);
            return null;
        } else {
            log.info("NpushIter 应用{} - {}日激活的用户分页查询结果: beginDateTime>>{}, endDateTime>>{}, 共{}人" , packageName, actDay, beginDateTime, endDateTime, list.size());
        }

        List<String> distinctIds = list.stream().map(DeviceInfo::getDistinct_id).distinct().collect(Collectors.toList());
        Optional<DeviceInfo> max = list.stream().max((a, b) -> {
            Date date1 = DateUtil.strToDate(a.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            Date date2 = DateUtil.strToDate(b.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            return date1.compareTo(date2);
        });

        DeviceInfoDto infoDto = new DeviceInfoDto(distinctIds, max.get().getCreateTime());
        list.clear();
        return infoDto;

    }
}
