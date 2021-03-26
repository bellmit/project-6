package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.common.constant.RedisConstant;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.common.utils.VersionUtil;
import com.miguan.reportview.entity.XyBuryingPoint;
import com.miguan.reportview.entity.XyVideoPlayover;
import com.miguan.reportview.mapper.XyVideoPlayoverMapper;
import com.miguan.reportview.service.IXyBuryingPointService;
import com.miguan.reportview.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Slf4j
@Service
@DS("clickhouse")
public class XyBuryingPointServiceImpl implements IXyBuryingPointService {

    @Resource
    private RedisService redisService;
    @Resource(name = "buryMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource
    private XyVideoPlayoverMapper xyVideoPlayoverMapper;

    private static final String table_name = "xy_burying_point";
    private String index_creat_time = null;

    @Override
    public void copyToClickHouseFromMongo(LocalDateTime localDateTime) {
        if(redisService.exits(RedisConstant.mongo_xy_bury_time_index)){
            index_creat_time = redisService.get(RedisConstant.mongo_xy_bury_time_index);
        }

        LocalDateTime ontMinuteAgo = localDateTime.minusSeconds(30L);
        String documentName = table_name + ontMinuteAgo.format(DateUtil.YYYYMMDD_FORMATTER);

        Criteria criteria = new Criteria();
        criteria.and("action_id").is("xy_video_playover");
        if (index_creat_time != null) {
            criteria.and("creat_time").gt(index_creat_time);
        }

        MatchOperation match = match(criteria);
        SortOperation sort = sort(Sort.by(Sort.Order.asc("creat_time")));
        LimitOperation limit = limit(5000);
        Aggregation agg = Aggregation.newAggregation(match, sort, limit);
        AggregationResults<XyBuryingPoint> aggVideoIdsObj = mongoTemplate.aggregate(agg, documentName, XyBuryingPoint.class);
        List<XyBuryingPoint> list = aggVideoIdsObj.getMappedResults();

        List<XyVideoPlayover> playoverList = new ArrayList<XyVideoPlayover>();
        list.stream().forEach(t -> {
            index_creat_time = t.getCreat_time();
            String app_version = t.getApp_version();
            if (StringUtils.equals(app_version, "2.7.4") || VersionUtil.compare(app_version, "2.8.0")) {
                Date dd = DateUtil.strToDate(index_creat_time, "yyyy-MM-dd HH:mm:ss");

                XyVideoPlayover playover = new XyVideoPlayover();
                playover.setUuid("");
                playover.setPackage_name(t.getApp_package());
                playover.setApp_version(app_version);
                playover.setChannel(t.getChannel_id());
                playover.setChange_channel(t.getChannel_id());
                int isNew = t.getIs_new() == 20 ? 0 : 1;
                playover.setIs_new(isNew);
                playover.setIs_new_app(isNew);
                playover.setModel("");
                playover.setDistinct_id(t.getDevice_id());
                playover.setReceive_time(dd);
                playover.setCreat_time(dd);
                playover.setCountry("");
                playover.setProvince("");
                playover.setCity("");
                playover.setVideo_id(t.getVideo_id() == null ? 0 : t.getVideo_id());
                playover.setCatid(t.getCatid() == null ? 0 : t.getCatid());
                playover.setVideo_time(StringUtils.isBlank(t.getVideo_time()) ? 0 : Integer.parseInt(t.getVideo_time()));
                playover.setPlay_time(t.getVideo_play_time());
                playover.setPlay_time_r(t.getVideo_play_time());
                playoverList.add(playover);
            }
        });

        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long expTime = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        redisService.set(RedisConstant.mongo_xy_bury_time_index, index_creat_time, expTime.intValue());
        if (CollectionUtils.isEmpty(playoverList)) {
            return;
        }

        xyVideoPlayoverMapper.saveBatch(playoverList);

    }
}
