package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.SimilarParamDto;
import com.miguan.bigdata.dto.SimilarVideoDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 视频标题和背景图片相似度查询service
 */
public interface VideoImgService {
    /**
     * 生成历史视频的背景图片的向量特征
     * @param startRow 开始记录数
     */
    void createHistoryImageVector(Integer startRow, String videoIds);

    /**
     * 根据视频标题、视频背景图片相似度查询
     * @param paramDto
     * @return
     */
    List<SimilarVideoDto> findSimVideoList(SimilarParamDto paramDto);

    /**
     * 查询出当前视频库中标题和封面图片有重复可能性的视频
     * @param startRow
     * @param videoIds
     * @param titleThreshold
     * @param imgThreshold
     */
    void repeatHistoryVideo(Integer type, Integer startRow, String videoIds, Double titleThreshold, Double imgThreshold);

    void syncImgVector(Integer from, Integer size);
}
