package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.entity.es.VideoEmbeddingEs;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.vo.RecVideosVo;

import java.util.List;

/**
 * 双塔向量service
 */
public interface EmbeddingService {

    /**
     * 在视频新增、发布、更新时生成视频向量
     * @param videoVo
     */
    void videoEmbedding(RecVideosVo videoVo);

    /**
     * 删除es中的视频向量
     * @param videoId
     */
    void deleteVideoEmbedding(String videoId);

    //获取用户手机应用的安装列表
     String findAppsList(String distinctId);

    /**
     * 从es中获取具体的用户向量数组值
     * @param uuid
     * @return
     */
    String getUserEmbeddingVector(String uuid);

    /**
     * 双塔向量视频召回
     * @param publicInfo
     * @param alredayVideos 热门召回的视频集合
     * @return 双塔向量视频召回集合
     */
    List<VideoHotspotVo> findFromEsVideoEmbedding(PublicInfo publicInfo, List<VideoHotspotVo> alredayVideos);

    /**
     * 根据用户向量数据，在es中用余弦相似度函数查询出相似度最高的近800个视频
     * @param vector
     * @return
     */
    public List<String> findVideoByVideoEmbeddingVector(String vector);

    /**
     * 从es中获取视频向量
     * @param videoId
     * @param videoUrl
     * @return
     */
    VideoEmbeddingEs getVideoEmbeddingVector(String videoId, String videoUrl);

    /**
     * 获取用户向量
     * @param publicInfo
     * @return
     */
    List<String> userEmbedding(PublicInfo publicInfo);

    /**
     * ES新增或更新图像特征向量
     * @param videoId
     * @param title
     * @param imgUrl 视频背景图片url
     */
    void saveEsImgVector(Integer videoId, String title, String imgUrl);
}
