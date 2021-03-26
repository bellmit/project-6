package com.miguan.laidian.repositories;


import com.miguan.laidian.entity.PushArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 消息推送表
 * @Author shixh
 * @Date 2019/9/10
 **/
public interface PushArticleDao extends JpaRepository<PushArticle, Long> {

    List<PushArticle> findByStateAndAppType(int state,String appType);

    //获取定时推送信息
    List<PushArticle> findAllByStateAndPushTypeAndAppType(int state, int pushType, String appType);

    //获取定时推送信息
    List<PushArticle> findAllByStateAndPushType(int state, int pushType);
}
