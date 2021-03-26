package com.miguan.laidian.common.strategy.pushStrategy;

import com.miguan.laidian.common.constants.AutoPushConstant;
import com.miguan.laidian.common.strategy.PushStrategy;
import com.miguan.laidian.entity.AutoPushConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 活动推送策略
 */
@Slf4j
public class ActivityPushStrategy extends PushStrategy {

    /**
     * 默认执行策略
     */
    @Override
    public void doexecute(){
        AutoPushConfig autoPushConfig = getAutoPushConfig();
        log.info("【自动推送】=====开始活动推送=====："+autoPushConfig.getId());
        log.info("配置id为："+autoPushConfig.getId());
        log.info("配置类型："+autoPushConfig.getEventType());
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
                List<Map<String,Object>> infoDataList = postDbInfo(autoPushConfig, sdf, appPackage, videoInfos, pageNum, pageSize,calendar);
                if(CollectionUtils.isEmpty(infoDataList)) break;
                //封装推送，如果无vedio则为-1
                fillInfoMap(infoDataList,videoInfos);

                if(pageNum % 5 == 0){
                    singleOrManyBatchPush(autoPushConfig, appPackage, videoInfos);

                    videoInfos = new HashMap<>();
                    continue;
                }


                if(MapUtils.isEmpty(videoInfos)){
                    break;
                }
            }
            if(MapUtils.isEmpty(videoInfos)){
                continue;
            }
            singleOrManyBatchPush(autoPushConfig, appPackage, videoInfos);
        }
        log.info("【自动推送】=====结束推送=====："+autoPushConfig.getId());
    }

}
