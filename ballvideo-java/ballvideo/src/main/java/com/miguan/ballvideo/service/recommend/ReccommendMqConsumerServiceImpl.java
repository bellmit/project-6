package com.miguan.ballvideo.service.recommend;

import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.repositories.VideoEsRepository;
import com.miguan.ballvideo.vo.video.RecVideosVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-25 
 *
 */
@Service
public class ReccommendMqConsumerServiceImpl {
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource
    private DynamicQuery dynamicQuery;
    @Resource
    private VideoEsRepository videoEsRepository;
    private String video_sql = "select v.id,v.title,v.cat_id,v.url_img,v.bsy_url AS bsyUrl,v.created_at AS createdAt," +
            "UNIX_TIMESTAMP(v.created_at) as createDate,v.bsy_img_url AS bsyImgUrl,v.collection_count AS collectionCount," +
            "(v.love_count + v.love_count_real) AS loveCount,v.comment_count AS commentCount,v.is_incentive AS incentiveVideo," +
            "v.user_id,IFNULL(g.id, 0) AS gatherId,g.title as gatherTitle,v.bsy_m3u8,v.share_count shareCount, v.fake_share_count fakeShareCount," +
            "(v.watch_count + v.watch_count_real) AS watchCount,'0' collection,'0' love,v.bsy_head_url AS bsyHeadUrl," +
            "v.video_author AS videoAuthor,v.video_time,v.video_size AS videoSize,v.base_weight+v.real_weight as totalWeight," +
            "v.encryption_android_url,v.encryption_ios_url,v.encryption_xcx_url,v.videos_source,v.state, v.online_date as onlineDate" +
            " FROM first_videos v left join video_gather g ON g.id = v.gather_id AND g.state = 1 where v.state = 1 ";

    public void updateVideo(String videoIds, String options) {
        if (VideoESOptions.videoAdd.name().equals(options) || VideoESOptions.directVideoAdd.name().equals(options)) {
            String sql = video_sql + " and v.id in(" + videoIds + ")";
            List<RecVideosVo> videos = dynamicQuery.nativeQueryList(RecVideosVo.class, sql);
            if (CollectionUtils.isEmpty(videos)) {
                return;
            }
            String weights = null;
            String weights1 = null;
            //  从redis获取初始权重值
            try (Jedis jedis = recDB9Pool.getResource()) {
                weights = jedis.get("bg_videoCount:allVpr");
                weights1 = jedis.get("bg_videoCount:allVptr");
            }
            Double weight = StringUtils.isBlank(weights) ? Double.valueOf(0) : Double.valueOf(weights);
            Double weight1 = StringUtils.isBlank(weights1) ? Double.valueOf(0) : Double.valueOf(weights1);
            for (RecVideosVo video : videos) {
                String onLinedate = DateUtil.dateStr4(video.getOnlineDate());
                videoHotspotService.addOrUpdateHotspot(video.getId().toString(), video.getIncentiveVideo() == 1 ? true : false, video.getCatId().intValue(), video.getState() == 1 ? 1 : 0, video.getGatherId().intValue(), onLinedate, video.getVideoTime(), weight, weight1);
            }
        }
    }

    public void deleteVideo(String videoIds, String options) {
        if (VideoESOptions.videoDelete.name().equals(options)) {
            String[] ids = videoIds.split(",");
            for (String id : ids) {
                videoHotspotService.deleteHotspot(id);  //设置普通视频权重表的状态为：禁用(mongodb)
                videoHotspotService.deleteIncentiveHotspot(id);  //设置激励视频权重表的状态为：禁用(mongodb)
            }
        }
    }

    public void updateByGatherId(Long gatherId, String videoIds) {
        List<String> vodeoIds_arrA = Arrays.asList(videoIds.split(","));
        int collId = gatherId.intValue();
        for (String videoId : vodeoIds_arrA) {
            //更新普通视频的权重信息到mongodb（es中的视频都是“上线状态”）
            videoHotspotService.updateHotspot(videoId, null, null, collId);
            //更新激励视频的权重信息到mongodb
            videoHotspotService.updateIncentiveHotspot(videoId, null, null, collId);
        }

    }

    public void deleteExpiredVideos() {
        int day = Global.getInt("init_video_mostRecentDays");
        long seconds = System.currentTimeMillis() / 1000 - 60 * 60 * 24 * day;
        List<FirstVideoEsVo> videos = this.videoEsRepository.findByCreateDateLessThanEqual(seconds);
        if (CollectionUtils.isNotEmpty(videos)) {
            //更新mongodb中的权重信息(状态统一改成禁用)
            for (FirstVideoEsVo video : videos) {
                videoHotspotService.updateHotspot(video.getId().toString(), null, 0, null);
                videoHotspotService.updateIncentiveHotspot(video.getId().toString(), null, 0, null);
            }
        }
    }
}
