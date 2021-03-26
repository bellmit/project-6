package com.miguan.laidian.springTask.thread;

import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.PushArticleService;
import com.miguan.laidian.service.PushSevice;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Data
public class PushArticleRunTask implements Runnable {

    private PushSevice uPushSevice;

    private PushArticle pushArticle;

    private Map<String, ScheduledFuture<?>> futuresMap;

    private PushArticleService pushArticleService;

    @Override
    public void run() {
        Long id = pushArticle.getId();
        RedisLock redisLock = new RedisLock(RedisKeyConstant.PUSH_TASK + id, RedisKeyConstant.PUSH_TASK_SECONDS);
        if (redisLock.lock()) {
            PushArticle pushArticleQuery = pushArticleService.findOne(id);
            if (pushArticleQuery == null || StringUtils.isBlank(pushArticleQuery.getTitle())
                    || StringUtils.isBlank(pushArticleQuery.getNoteContent())) {
                log.info("当前没有符合条件的推送数据");
            } else {
                log.info(Thread.currentThread().getName() + "|schedule PushArticleRunTask" + "|" + pushArticle.getTitle() + "|" + pushArticle.getPushTime());
                //调用推送接口
                uPushSevice.sendInfoToMobile(pushArticle);
            }
            futuresMap.remove(String.valueOf(id));
        }
    }
}