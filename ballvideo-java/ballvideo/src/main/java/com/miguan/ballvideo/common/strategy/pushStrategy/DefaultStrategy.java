package com.miguan.ballvideo.common.strategy.pushStrategy;

import com.miguan.ballvideo.common.constants.AutoPushConstant;
import com.miguan.ballvideo.common.strategy.PushStrategy;
import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.vo.AutoPushInfo;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 新用户策略
 */
@Slf4j
public class DefaultStrategy extends PushStrategy {

    /**
     * 默认执行策略
     */
    @Override
    public void doexecute(){
        AutoPushConfig autoPushConfig = getAutoPushConfig();
        log.info("=====开始推送=====："+autoPushConfig.getId());
        log.info("配置id为："+autoPushConfig.getId());
        log.info("配置类型："+autoPushConfig.getTriggerEvent());
        log.info("推送时间类型："+autoPushConfig.getTriggerType());
        log.info("推送时间："+autoPushConfig.getTriggerTime());

        Calendar calendar = Calendar.getInstance();
        if(AutoPushConstant.TRIGGER_TYPE_TOMON.equals(autoPushConfig.getTriggerType())){
            calendar.add(Calendar.DATE,-1);
        }
        String appPackageStr = autoPushConfig.getAppPackage();
        String[] appPackages = appPackageStr.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String appPackage:appPackages) {
            Map<Long,Set<String>> videoInfos = new HashMap<>();
            //1.先从大数据那,获取用户信息. (满足条件的用户信息) 拿到的是distinctId和videoid
            int pageNum = 0 ;
            int pageSize = 1000;
            while (true){
                pageNum ++;
                boolean b = postDbInfo(autoPushConfig, sdf, appPackage, videoInfos, pageNum, pageSize,calendar);
                if(!b) break;
                if(pageNum % 5 == 0){
                    List<AutoPushInfo> autoPushInfos = fillAutoPushInfo(appPackage, videoInfos);
                    log.info(appPackage + "推送量autoPushInfos.size："+autoPushInfos.size());
                    autoPushService.batchPush(autoPushInfos,autoPushConfig);
                    videoInfos = new HashMap<>();
                    continue;
                }
                if(videoInfos.size() == 0){
                    break;
                }
            }
            if(videoInfos == null || videoInfos.size() == 0){
                continue;
            }
            List<AutoPushInfo> autoPushInfos = fillAutoPushInfo(appPackage, videoInfos);
            log.info(appPackage + "推送量autoPushInfos.size："+autoPushInfos.size());
            autoPushService.batchPush(autoPushInfos,autoPushConfig);
        }
        log.info("=====结束推送=====："+autoPushConfig.getId());
    }
}
