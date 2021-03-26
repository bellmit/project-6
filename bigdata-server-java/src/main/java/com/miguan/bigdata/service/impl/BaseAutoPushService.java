package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.constant.RedisKeyConstants;
import com.miguan.bigdata.common.enums.LdPushUserEnmu;
import com.miguan.bigdata.dto.PushDto;
import com.miguan.bigdata.entity.mongo.AppDeviceVo;
import com.miguan.bigdata.mapper.PushLdMapper;
import com.miguan.bigdata.mapper.PushVideoMapper;
import com.miguan.bigdata.service.AppDeviceService;
import com.miguan.bigdata.service.Redis2Service;
import com.miguan.bigdata.vo.PushResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaseAutoPushService {
    @Resource
    private PushVideoMapper pushVideoMapper;
    @Resource
    private PushLdMapper pushLdMapper;
    @Resource
    private Redis2Service redis2Service;
    @Resource(name = "idMappingMongoTemplate")
    private MongoTemplate idMappingMongoTemplate;

    public List<PushResultVo> findPushUserList(PushDto pushDto) {
        String redisKey = RedisKeyConstants.push_search_index + pushDto.getDd() + pushDto.getPackageName() + pushDto.getType() + pushDto.getMsgId();
        String redisValue = redis2Service.get(redisKey);

        int startRow = 0;
        int endRow = pushDto.getPageSize();
        if (StringUtils.isNoneEmpty(redisValue)) {
            startRow = Integer.parseInt(redisValue);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("packageName", pushDto.getPackageName());

        List<PushResultVo> finalList = new ArrayList<PushResultVo>();
        int thirtyDaysAgo = Integer.parseInt(LocalDate.now().minusDays(30L).format(DateTimeFormatter.BASIC_ISO_DATE));
        do{
            //分页参数
            params.put("startRow", startRow);
            params.put("endRow", endRow);
            List<PushResultVo> result = null;
            if(StringUtils.equals(pushDto.getPackageName(), "com.mg.phonecall")){
                params.put("dd", pushDto.getDd());
                params.put("userType", pushDto.getType());
                if (LdPushUserEnmu.getIsNeedVideo(pushDto.getType())) {
                    result = pushLdMapper.findLdAutoPushList(params);
                } else {
                    result = pushLdMapper.findLdAutoPushUserList(params);
                }
            } else {
                //根据参数生成推送结果表名
                int dt = Integer.parseInt(pushDto.getDd().replace("-", ""));
                String tableName = "push_" + pushDto.getPackageName().replace(".", "_") + "_" + pushDto.getType() + "_" + dt;
                params.put("tableName", tableName);
                result = pushVideoMapper.findPushVideosResult(params);
            }

            if(CollectionUtils.isEmpty(result)){
                break;
            }
            this.youMengPushFilter(pushDto, result, thirtyDaysAgo);
            finalList.addAll(result);
            startRow += result.size();
            endRow = pushDto.getPageSize() - finalList.size();
        } while (endRow > 0);

        redis2Service.set(redisKey, startRow, 600);
        return finalList;
    }

    private void youMengPushFilter(PushDto pushDto, List<PushResultVo> resultVoList, int thirtyDaysAgo) {
        List<String> distinctList = resultVoList.stream().map(PushResultVo::getDistinctId).collect(Collectors.toList());
        Query query = new Query();
        query.addCriteria(Criteria.where("package_name").is(pushDto.getPackageName()));
        query.addCriteria(Criteria.where("distinct_id").in(distinctList));
        List<AppDeviceVo> deviceList = idMappingMongoTemplate.find(query, AppDeviceVo.class, AppDeviceService.collection_name);
        Map<String, List<AppDeviceVo>> distinctAndPushChannelMap = deviceList.stream().collect(Collectors.groupingBy(AppDeviceVo::getDistinct_id));

        Iterator<PushResultVo> iterator = resultVoList.iterator();
        while(iterator.hasNext()){
            PushResultVo vo = iterator.next();
            List<AppDeviceVo> appDeviceVoList = distinctAndPushChannelMap.get(vo.getDistinctId());
            if(CollectionUtils.isEmpty(appDeviceVoList)){
                log.warn("自动推送：设备{}未查询到推送渠道，直接跳过友盟判断", vo.getDistinctId());
                continue;
            }
            AppDeviceVo appDeviceVo = appDeviceVoList.get(0);
            if (appDeviceVo.getNpush_channel() != null && appDeviceVo.getNpush_channel() == 1) {
                if(appDeviceVo.getLast_visit_date() >= thirtyDaysAgo){
                    log.debug("自动推送：设备{},推送渠道为友盟,并且最新登录时间在30天内", vo.getDistinctId());
                } else {
                    log.debug("自动推送：设备{},推送渠道为友盟,并且最新登录时间大于30天,不进行推送", vo.getDistinctId());
                    iterator.remove();
                }
            }
        }
    }

}
