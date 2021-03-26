package com.miguan.recommend.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.entity.mongo.CatHotspotVo;
import com.miguan.recommend.service.ck.DwVideoActionService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CatHotspotTask {
    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private VideosCatService videosCatService;
    @Resource
    private DwVideoActionService dwVideoActionService;
    @Autowired
    private MongoTemplate logMongoTemplate;
    /**
     * 每天的00:00:01执行
     */
    @Scheduled(cron = "30 0 0 * * ?")
    public void initCatHotspot() {
        long incr = redisDB0Service.incr("initCatHotspot", ExistConstants.five_minutes_seconds);
        if (incr == 1) {
            log.warn("=======================相似分类初始化开始=======================");
            String yesterDay = LocalDate.now().minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<String> catList = videosCatService.getAllCatIds(XyConstants.FIRST_VIDEO_CODE);
            log.info("所有视频分类>>{}", JSONObject.toJSONString(catList));
            List<CatHotspotVo> catHotspotVoList = new ArrayList<CatHotspotVo>();
            for (String cat : catList) {
                List<Map<String, Object>> currentSimilarCats = dwVideoActionService.findSimilarCatid(yesterDay, cat);
                if(CollectionUtils.isEmpty(currentSimilarCats)){
                    log.info("视频分类{}的相似分类为空, ", cat);
                    continue;
                }
                log.info("视频分类{}的相似分类>>{}", cat, JSONObject.toJSONString(currentSimilarCats));
                int parentCat = Integer.parseInt(cat);
                double weight = 1D;
                for(Map<String, Object> e : currentSimilarCats) {
                    Integer similarCat = (Integer) e.get("catid");
                    if(cat.equals(similarCat.toString()) || similarCat.toString().equals("0")){
                        continue;
                    }
                    CatHotspotVo catHotspotVo = new CatHotspotVo(parentCat, similarCat, weight);
                    catHotspotVoList.add(catHotspotVo);
                    ++weight;
                }
            }

            if (CollectionUtils.isEmpty(catHotspotVoList)) {
                log.info("无最新相似分类，沿用上一日的数据");
                return;
            }
            log.info("最新相似分类的数据>>{}", JSONObject.toJSONString(catHotspotVoList));
            Query query = new Query();
            query.addCriteria(Criteria.where("parent_catid").gt(0));
            logMongoTemplate.remove(query, "cat_hotspot");
            logMongoTemplate.insertAll(catHotspotVoList);
            log.warn("=======================相似分类初始化结束=======================");
        }
    }

}
