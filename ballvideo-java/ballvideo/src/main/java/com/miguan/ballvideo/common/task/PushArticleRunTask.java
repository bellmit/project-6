package com.miguan.ballvideo.common.task;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Data
public class PushArticleRunTask implements Runnable {

    private PushArticle pushArticle;

    private PushArticleConfigService pushArticleConfigService;

    private PushArticleSendResultService pushArticleSendResultService;

    private ClUserService userService;

    private Map<String, ScheduledFuture<?>> futuresMap;

    private PushArticleService pushArticleService;

    private PushSevice pushSevice;

    private ClUserOpinionService clUserOpinionService;

    @Override
    public void run() {

        RedisLock redisLock = new RedisLock(RedisKeyConstant.PUSH_TASK+pushArticle.getId(), RedisKeyConstant.PUSH_TASK_SECONDS);
        if (redisLock.lock()) {
            PushArticle pushArticleQuery = pushArticleService.findOneToPush(pushArticle.getId());
            if (pushArticleQuery == null || StringUtils.isBlank(pushArticleQuery.getTitle())
                    || StringUtils.isBlank(pushArticleQuery.getNoteContent())) {
                log.info("当前没有符合条件的推送数据");
            } else {
                log.info("###################################定时任务开始执行###################################");
                log.info(Thread.currentThread().getName() + "|schedule PushArticleRunTask" + "|" + pushArticle.getTitle() + "|" + pushArticle.getPushTime());
                String appPackage = pushArticle.getAppPackage();

                // 实时推送前，新增到系统消息中去 saveClUserOpinionPlus
                clUserOpinionService.saveClUserOpinionByPushArticle(pushArticle);

                /* 旧的写法限制了 IOS 的 appPackage 名称，新增常量 List，判断包名称的时候，取 List 来判断 */
                //if (Constant.IOSPACKAGE.equals(appPackage)) {
                if (Constant.IOSPACKAGELIST.contains(appPackage)) {
                    //IOS推送
                    pushSevice.sendInfoToIOS(pushArticle,null);
                } else {
                    try {
                        //Android推送
                        pushSevice.sendInfoToAndroid(pushArticle,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("定时推送发生异常， 异常信息--->" + e.getMessage());
                        log.error("发送失败，请联系管理员");
                    }
                }
            }
            futuresMap.remove(String.valueOf(pushArticle.getId()));
        }
    }
}