package com.miguan.ballvideo.common.util.video;

import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.entity.UserLabelGrade;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisDB8Service;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author shixh
 * @Date 2020/2/21
 **/
public class VideoSQLUtils {

    public static String getSQL(RedisDB8Service redisService, VideoParamsDto params, String[] catIdsSort, String[] duty, String incentive) {
        String catId1 = catIdsSort[0];
        int limit1 = Integer.parseInt(duty[0]);
        int limit2 = Integer.parseInt(duty[1]);
        String showedIds1 = getShowedIds(redisService, params.getDeviceId(), catId1);
        String gatherIds = params.getGatherIds();//需要屏蔽的集合id
        String otherCatIds = params.getOtherCatIds();
        StringBuffer buffer = getSQLBuffer(catId1, limit1, params.getUserId(), showedIds1, gatherIds, otherCatIds,incentive);
        List<String> catIds = Arrays.asList(catIdsSort).subList(1, limit2 + 1);
        for (String catId : catIds) {
            String showedIds = getShowedIds(redisService, params.getDeviceId(), catId);
            buffer.append(" union all ").append(getSQLBuffer(catId, 1, params.getUserId(), showedIds, gatherIds, otherCatIds,incentive));
        }
        return buffer.toString();
    }

    private static String getShowedIds(RedisDB8Service redisService, String deviceId, String catId1) {
        String key = RedisKeyConstant.SHOWEDIDS_KEY + deviceId + ":" + catId1;
        String showedIds = redisService.get(key);
        if (StringUtils.isNotBlank(showedIds)) {
            if (showedIds.split(",").length > 100) {
                redisService.del(key);
                return null;
            }
        }
        return showedIds;
    }

    private static StringBuffer getSQLBuffer(String catId, int limit, String userId, String showedIds, String gatherIds, String otherCatIds, String incentive) {
        StringBuffer sb = new StringBuffer("");
        if (StringUtils.isNotBlank(userId) && !"0".equals(userId)) {
            sb.append("(SELECT v.id,v.cat_id,v.title,v.url,v.url_audio,v.url_img,v.bsy_url AS bsyUrl,v.bsy_img_url AS bsyImgUrl,v.collection_count AS collectionCount,v.user_id,");
            sb.append("(v.love_count + v.love_count_real) AS loveCount,v.comment_count AS commentCount,(v.watch_count + v.watch_count_real) AS watchCount,v.video_size AS videoSize,");
            sb.append("v.created_at AS createdAt,IFNULL(g.id, 0) AS gatherId,g.title AS gatherTitle,v.state,v.bsy_head_url AS bsyHeadUrl,");
            sb.append("v.video_author AS videoAuthor,v.video_time,v.share_count,v.fake_share_count,");
            sb.append("(CASE WHEN cv.collection = '1' THEN '1' ELSE '0' END	) collection,");
            sb.append("v.base_weight + v.real_weight AS totalWeight,");
            sb.append("(CASE WHEN cv.love = '1' THEN '1' ELSE '0' END ) love,v.bsy_m3u8,v.encryption_android_url,v.encryption_ios_url,v.videos_source,v.xcx_share_img ");
            sb.append("FROM first_videos v LEFT JOIN cl_user_videos cv ON v.id = cv.video_id AND cv.video_type = 10 ");
            sb.append("AND cv.user_id = ").append(userId).append(" left join video_gather g ON g.id = v.gather_id AND g.state = 1 ");
            sb.append("WHERE v.state = 1 and v.bsy_img_url != '' AND v.bsy_url != '' and (cv.interest != '1' OR cv.interest IS NULL) ");
            sb.append("AND v.cat_id = ").append(catId).append(" ");
            if (showedIds != null) {
                sb.append(" AND v.id NOT in (").append(showedIds).append(") ");
            }
            if (gatherIds != null) {
                sb.append(" AND v.gather_id  NOT in (").append(gatherIds).append(") ");
            }
            sb.append(" AND v.gather_id = 0 ");  //屏蔽合集视频
            if (StringUtils.isNotBlank(otherCatIds)) {
                sb.append(" AND v.cat_id NOT in(").append(otherCatIds).append(") ");
            }
            if (StringUtils.isNotBlank(incentive)) {
                sb.append(" AND v.is_incentive = ").append(incentive).append(" ");
            }
            sb.append("ORDER BY v.base_weight + v.real_weight DESC LIMIT ").append(limit).append(")");
        } else {
            sb.append("(SELECT  ");
            sb.append("v.id,");
            sb.append("v.title,");
            sb.append("v.cat_id,");
            sb.append("v.url_img,");
            sb.append("v.bsy_url AS bsyUrl,");
            sb.append("v.created_at AS createdAt,");
            sb.append("v.bsy_img_url AS bsyImgUrl,");
            sb.append("v.collection_count AS collectionCount,");
            sb.append("(v.love_count + v.love_count_real) AS loveCount,");
            sb.append("v.comment_count AS commentCount,");
            sb.append("IFNULL(g.id, 0) AS gatherId,");
            sb.append("g.title AS gatherTitle,");
            sb.append("(v.watch_count + v.watch_count_real) AS watchCount,");
            sb.append("'0' collection,");
            sb.append("'0' love,");
            sb.append("v.bsy_head_url AS bsyHeadUrl,");
            sb.append("v.video_author AS videoAuthor,");
            sb.append("v.video_time,");
            sb.append("v.state,");
            sb.append("v.video_size AS videoSize,v.user_id,");
            sb.append("v.base_weight + v.real_weight AS totalWeight, ");
            sb.append("v.bsy_m3u8,v.encryption_android_url,v.encryption_ios_url,v.videos_source,v.xcx_share_img ");
            sb.append("FROM first_videos v LEFT JOIN video_gather g ON g.id = v.gather_id AND g.state = 1 ");
            sb.append("WHERE v.state = 1 AND v.bsy_url != '' AND v.bsy_img_url != '' AND v.cat_id = ").append(catId);
            if (showedIds != null) {
                sb.append(" AND v.id NOT in (").append(showedIds).append(") ");
            }
            if (gatherIds != null) {
                sb.append(" AND v.gather_id  NOT in (").append(gatherIds).append(") ");
            }
            sb.append(" AND v.gather_id = 0 ");  //屏蔽合集视频
            if (StringUtils.isNotBlank(otherCatIds)) {
                sb.append(" AND v.cat_id NOT in(").append(otherCatIds).append(") ");
            }
            if (StringUtils.isNotBlank(incentive)) {
                sb.append(" AND v.is_incentive = ").append(incentive).append(" ");
            }
            sb.append(" ORDER BY v.base_weight + v.real_weight DESC LIMIT ").append(limit).append(")");
        }
        return sb;
    }

    public static String getBatchUpdateSQL(Map<Long, Long> params, List<String> ids) {
        StringBuffer sb = new StringBuffer("update first_videos ");
        sb.append("set real_weight=(case");
        for (Map.Entry<Long, Long> entry : params.entrySet()) {
            sb.append(" when id = ").append(entry.getKey()).append(" then ").append(entry.getValue());
            ids.add(entry.getKey() + "");
        }
        sb.append(" end) where ID in (").append(String.join(",", ids)).append(")");
        return sb.toString();
    }

    public static String calculateByIds(String showedIds) {
        StringBuffer sb = new StringBuffer("");
        sb.append("select v.id,v.real_weight as realWeightDb,FLOOR(count(c.id) * 3 + (sqrt(v.play_all_count) + sqrt(v.play_count)) / 2 + v.share_count * 3 ) as realWeightCalculate ");
        sb.append("from first_videos v ");
        sb.append("left join cl_user_videos c on c.video_id = v.id and c.video_type = 10 and c.collection='1' ");
        sb.append("where v.id in (").append(showedIds).append(") ");
        sb.append("group by v.id,v.share_count,v.play_all_count,v.play_count,v.real_weight");
        return sb.toString();
    }

    public static String getInsertValuesByUserLabelGrades(List<UserLabelGrade> userLabelGrades) {
        StringBuffer sb = new StringBuffer("");
        for (UserLabelGrade userLabelGrade : userLabelGrades) {
            sb.append("('").append(userLabelGrade.getId()).append("', '").append(userLabelGrade.getDeviceId()).append("', '").append(userLabelGrade.getCatId()).append("', '").append(userLabelGrade.getCatGrade()).append("'),");
        }
        String sqlBuffer = sb.toString();
        return sqlBuffer.substring(0, sqlBuffer.length() - 1);
    }

    public static String getInsertValuesByUserLabels(List<UserLabel> userLabels) {
        StringBuffer sb = new StringBuffer("");
        for (UserLabel userLabel : userLabels) {
            sb.append("('").append(userLabel.getId()).append("', '").append(userLabel.getCatId1()).append("', '").append(userLabel.getCatId2()).append("', '").append(userLabel.getDeviceId()).append("', '").append(userLabel.getCatIdsSort()).append("'),");
        }
        String sqlBuffer = sb.toString();
        return sqlBuffer.substring(0, sqlBuffer.length() - 1);
    }

}
