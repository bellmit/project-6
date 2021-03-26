package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.PushArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 消息推送表
 * @Author shixh
 * @Date 2019/9/10
 **/
public interface PushArticleDao extends JpaRepository<PushArticle, Long> {

    List<PushArticle> findByState(int state);

    //获取定时推送信息
    List<PushArticle> findAllByStateAndPushType(int state, int pushType);

    List<PushArticle> findByTypeAndState(Integer type,int state);
}
