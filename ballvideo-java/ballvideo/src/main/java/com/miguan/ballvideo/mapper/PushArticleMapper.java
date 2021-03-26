package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import com.miguan.ballvideo.vo.PushArticleVo;
import com.miguan.ballvideo.vo.video.VideoExamineVo;

import java.util.Map;

/**
 * 审核申请 mapper
 * @author daoyu
 * @date 2020-06-03
 **/

public interface PushArticleMapper {

    /**
     *
     * 新增用户意见反馈信息
     * @param pushArticleVo
     * @return
     **/
    int savePushArticle(PushArticleVo pushArticleVo);

}