package com.miguan.laidian.service;


import com.miguan.laidian.entity.PushArticle;

import java.util.List;

/**
 * Created by shixh on 2019/9/10.
 */
public interface PushArticleService {

    PushArticle findOneToPush(Long id);

    //获取定时推送信息
    List<PushArticle> findAllFixedTimeListToPush();

    PushArticle findOne(Long id);
}
