package com.miguan.reportview.service;

import com.miguan.reportview.entity.VideosCat;
import com.miguan.reportview.vo.FirstVideosStaVo;

import java.util.List;

/**
 * <p>
 * 视频类别表 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface IVideosService {

    String getCatName(String catid);

    List<VideosCat> getVideosCat();

    List<FirstVideosStaVo> staAddVideo(String date);

    List<FirstVideosStaVo> staOfflineVideo(String date);

    List<FirstVideosStaVo> staAllVideo();

    List<FirstVideosStaVo> staNewOnlineVideo(String date);

    List<FirstVideosStaVo> staNewOfflineVideo(String date);

    List<FirstVideosStaVo> staOlineVideo(String date);

    List<FirstVideosStaVo> staNewCollectVideo(String date);

    void syncVideoInfo();

    /**
     * 汇总视频明细数据到clickhouse的video_detail表中
     *
     * @param day
     */
    void syncVideoDetail(String day);

    /**
     * 从mysql来电库中的videos中同步来电秀数据到clickhouse的ld_video_info中
     */
    void syncLdVideoInfo();
}
