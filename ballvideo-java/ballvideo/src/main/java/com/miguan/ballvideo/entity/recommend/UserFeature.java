package com.miguan.ballvideo.entity.recommend;

import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.recommend.IPService;
import com.miguan.ballvideo.vo.mongodb.UserOfflineLabel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Data
@Slf4j
@Service
public class UserFeature {
    private int activeDay;
    private List<String> catPoolList;
    private Map<String, Double> catFav;
    private String city;
    private PublicInfo publicInfo;

    @Resource(name = "featureMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;
    @Autowired
    private IPService ipService;

    public UserFeature() {

    }

    public UserFeature(int activeDay, List<String> catPoolList, Map<String, Double> catFav, PublicInfo publicInfo) {
        this.activeDay = activeDay;
        this.catPoolList = catPoolList;
        this.catFav = catFav;
        this.publicInfo = publicInfo;
    }

    public void initUserFeature(PublicInfo publicInfo) {
        this.publicInfo = publicInfo;
        this.activeDay = 0;
        this.catFav = new LinkedHashMap<>();
        String uuid = publicInfo.getUuid();
        if (StringUtil.isEmpty(uuid)) {
            return;
        }

        final LocalDate localDate = LocalDate.now();
        String collectionName = "userOffline_label_" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(!mongoTemplate.collectionExists(collectionName)){
            collectionName = "userOffline_label_" + localDate.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        List<UserOfflineLabel> offlineLabels = mongoTemplate.find(query, UserOfflineLabel.class, collectionName);
        log.warn("{} 推荐获取用户离线特征返回条数：{}" , uuid, offlineLabels.size());

        if (!CollectionUtils.isEmpty(offlineLabels)) {
            this.activeDay = offlineLabels.get(0).getActive_day();
            Map<String, Double> offCatFv = new HashMap<String, Double>(offlineLabels.size());
            offlineLabels.stream().forEach(e ->{
                offCatFv.put(e.getCatid().toString(), new BigDecimal(e.getCat_fav()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue());
            });
            this.catFav = offCatFv;
            log.warn("{} 推荐获取到的mongo特征数据：activeDay>>{}, catFav>>{}", uuid, activeDay, catFav);
        }

        String[] ipInfo = ipService.getCurrentIpInfo();
        city = ipInfo.length > 2 ? ipInfo[3] : "";
        try (Jedis con = jedisPool.getResource()) {
            // 获取用户标签池
            String catPool = con.hget(RedisKeyConstant.USER_LIKE_CAT_POOL, uuid);
            this.catPoolList = StringUtil.isEmpty(catPool) ? null : Arrays.asList(catPool.split(","));
        }
    }
}
