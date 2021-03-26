package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.PushArticle;

import java.util.List;

/**
 * Created by shixh on 2019/9/10.
 */
public interface PushArticleService {
    PushArticle getOneToPush();

    PushArticle findOneToPush(Long id);

    List<PushArticle> findByType(Integer type);

    //获取定时推送信息
    List<PushArticle> findFixedTimeListToPush();

    List<PushArticle> cleanPushInfo();
}
