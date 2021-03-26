package com.miguan.laidian.service.impl;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.RandomUtil;
import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.entity.ActActivityConfig;
import com.miguan.laidian.entity.ActUserDrawRecord;
import com.miguan.laidian.entity.ActUserPrizeExchangeRecord;
import com.miguan.laidian.mapper.ActActivityConfigMapper;
import com.miguan.laidian.mapper.ActActivityMapper;
import com.miguan.laidian.mapper.ActSignRecordMapper;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.ActUserDrawRecordService;
import com.miguan.laidian.service.ActUserPrizeExchangeRecordService;
import com.miguan.laidian.service.ActivityConfigService;
import com.miguan.laidian.service.ActivityService;
import com.miguan.laidian.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tool.util.BeanUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Service
@Transactional
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    @Resource
    private ActActivityMapper actActivityMapper;
    @Resource
    private ActActivityConfigMapper actActivityConfigMapper;
    @Resource
    private ActivityConfigService activityConfigService;
    @Resource
    private RedisService redisService;
    @Resource
    private ActUserDrawRecordService userDrawRecordService;
    @Resource
    private ActUserPrizeExchangeRecordService actUserPrizeExchangeRecordService;
    @Resource
    private ActSignRecordMapper actSignRecordMapper;

    /**
     * 获取活动页面信息
     *
     * @param commomParams
     * @return
     */
    @Override
    public ActivityPageInfoVo activityPageInfo(CommonParamsVo commomParams) {
        final ActActivity actActivity = this.getCurActivityInfo(commomParams);
        if (actActivity == null) {
            throw new CommonException(888, "活动已结束");
        }
        //转盘
        List<ActActivityConfig> activityConfigs = activityConfigService.getActConfigByActId(actActivity.getId());
        //碎片商城
        List<ActivityConfigVo> configVos = actActivityConfigMapper.getUserActConfigById(actActivity.getId(), commomParams.getDeviceId());
        configVos = configVos.stream().filter(s -> s.getSort() != 0).collect(Collectors.toList());//过滤再来一次的商品
        //会员剩余抽奖次数
        Integer userDrawNum = this.getUserDrawNum(actActivity, commomParams.getDeviceId());
        ActivityPageInfoVo pageInfoVo = new ActivityPageInfoVo();
        pageInfoVo.setActActivity(actActivity);
        pageInfoVo.setActivityConfigs(activityConfigs);
        pageInfoVo.setActivityProducts(configVos);
        pageInfoVo.setUserDrawNum(userDrawNum);
        return pageInfoVo;
    }

    //滚动播报
    private List<Map<String, String>> getScrollBroadcast(List<ActActivityConfig> activityConfigs) {
        List<Map<String, String>> dataList = new ArrayList<>();
        if (!activityConfigs.isEmpty()) {
            //过滤再来一次的商品
            List<String> names = activityConfigs.stream().filter(s -> s.getSort() != 0).map(a -> a.getName()).collect(Collectors.toList());
            for (int i = 0; i < 30; i++) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("phone", RandomUtil.getRandomNum(4));
                dataMap.put("product", names.get(RandomUtil.getRandomRange(names.size(), 0)));
                dataList.add(dataMap);
            }
        }
        return dataList;
    }

    //会员剩余抽奖次数
    private Integer getUserDrawNum(ActActivity actActivity, String deviceId) {
        String key = RedisKeyConstant.ACTIVITY_USER_DRAW_NUM + actActivity.getId() + ":" + deviceId;
        if (redisService.exits(key)) {
            return Integer.parseInt(redisService.get(key));
        }
        Integer userDrawNum = actActivity.getDayDrawNum();
        redisService.set(key, userDrawNum, DateUtil.caluRedisExpiredTime());
        return userDrawNum;
    }

    /**
     * 活动首页任务列表
     *
     * @param commomParams
     * @return
     */
    @Override
    public List<ActivityTaskVo> getActivityTasks(CommonParamsVo commomParams) {
        final ActActivity actActivity = this.getCurActivityInfo(commomParams);
        List<ActivityTaskVo> taskVos = new ArrayList<>();
        for (int i = 2; i < 6; i++) {
            ActivityTaskVo taskVo = new ActivityTaskVo();
            if (i == Constant.activity_task_ldx) {
                if (actActivity.getLdxTaskFlag() == 0) {
                    continue;
                }
            } else if (i == Constant.activity_task_ls) {
                if (actActivity.getLsTaskFlag() == 0) {
                    continue;
                }
            } else if (i == Constant.activity_task_video) {
                if (actActivity.getVideoTaskFlag() == 0) {
                    continue;
                }
            } else if (i == Constant.activity_task_share) {
                if (actActivity.getShareTaskFlag() == 0) {
                    continue;
                }
            }
            taskVo.setType(i);
            taskVo.setState(this.getTaskState(taskVo.getType(), commomParams.getDeviceId(), actActivity.getId()));
            taskVos.add(taskVo);
        }
        return taskVos;
    }

    @Override
    public List<ActSignVo> getActSignTask(CommonParamsVo commomParams) {
        final ActActivity actActivity = this.getCurActivityInfo(commomParams);
        Long activityId = actActivity.getId();
        String deviceId = commomParams.getDeviceId();
        List<ActSignVo> list = new ArrayList<>();
        Date date = new Date();
        Date yesterday = DateUtil.getDateBefore(-1, date);
        String yesDateStr = tool.util.DateUtil.dateStr2(yesterday);//昨天
        String todayDateStr = tool.util.DateUtil.dateStr2(date);//今天
        Map<String, Object> param = new HashMap<>();
        param.put("activityId", activityId);
        param.put("deviceId", deviceId);
        param.put("signTime", yesDateStr);
        ActSignRecordVo actSignRecordVo = actSignRecordMapper.queryActSignRecord(param);
        String signKey = RedisKeyConstant.ACTIVITY_TASK_SIGN_IN + activityId + ":" + deviceId;
        String dateStr = "";
        if (redisService.exits(signKey)) {
            dateStr = redisService.get(signKey);
        }
        if (actSignRecordVo == null || actSignRecordVo.getDay() == 7) {
            for (int i = 0; i < 7; i++) {
                ActSignVo actSignVo = new ActSignVo();
                actSignVo.setType(Constant.activity_task_sign_in);
                actSignVo.setDay(i + 1);
                actSignVo.setState(0);
                actSignVo.setIsToday(0);
                if (i == 0){
                    actSignVo.setIsToday(1);
                    if(todayDateStr.equals(dateStr)) {
                        actSignVo.setState(1);
                    }
                }
                list.add(actSignVo);
            }
        } else {
            int day = actSignRecordVo.getDay();
            for (int i = 0; i < day; i++) {
                ActSignVo actSignVo = new ActSignVo();
                actSignVo.setType(Constant.activity_task_sign_in);
                actSignVo.setDay(i + 1);
                actSignVo.setIsToday(0);
                actSignVo.setState(1);//已签到
                list.add(actSignVo);
            }
            for (int i = day; i < 7; i++) {
                ActSignVo actSignVo = new ActSignVo();
                actSignVo.setType(Constant.activity_task_sign_in);
                actSignVo.setDay(i + 1);
                actSignVo.setIsToday(0);
                actSignVo.setState(0);
                if (i == day){
                    actSignVo.setIsToday(1);
                    if (todayDateStr.equals(dateStr)) {
                        actSignVo.setState(1);
                    }
                }
                list.add(actSignVo);
            }
        }
        return list;
    }

    private Integer getTaskState(Integer type, String deviceId, Long activityId) {
        Integer state = 0;
        if (type == Constant.activity_task_video) {
            //每日任务：观看视频新增每日观看次数限制，单日单个设备ID限制50次观看消息；
            String watchVideoKey = RedisKeyConstant.ACTIVITY_TASK_VIDEO + activityId + ":" + deviceId;
            if (redisService.exits(watchVideoKey)) {
                int a = Integer.valueOf(redisService.get(watchVideoKey));
                if (a >= 50) {
                    state = 1;
                }
            }
            return state;
        }
        String key = "";
        if (type == Constant.activity_task_ldx) {//每日成功设置来电秀
            key = RedisKeyConstant.ACTIVITY_TASK_SETTING_LDX + activityId + ":" + deviceId;
        } else if (type == Constant.activity_task_ls) {//每日成功设置来电铃声
            key = RedisKeyConstant.ACTIVITY_TASK_SETTING_LS + activityId + ":" + deviceId;
        } else if (type == Constant.activity_task_share) {//每日分享活动
            key = RedisKeyConstant.ACTIVITY_TASK_SHARE + activityId + ":" + deviceId;
        }
        if (redisService.exits(key)) {
            state = 1;
        }
        return state;
    }

    /**
     * 获取正在进行中的活动信息
     *
     * @return
     */
    @Override
//    @Cacheable(value = CacheConstant.GET_CUR_ACTIVITY_INFO, unless = "#result == null || #result.size()==0")
    public ActActivity getCurActivityInfo(CommonParamsVo commomParams) {
        Date curDate = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("curDate", curDate);
        params.put("appVersion", commomParams.getAppVersion());
        params.put("channelId", commomParams.getChannelId());
        List<ActActivity> actActivitys = actActivityMapper.findActActivity(params);
        if (!actActivitys.isEmpty()) {
            return actActivitys.get(0);
        }
        return null;
    }

    public ActActivity getActivityInfo(CommonParamsVo commomParams) {
        ActActivity curActivity = this.getCurActivityInfo(commomParams);
        //特殊处理给前端做判断
        if (curActivity == null) {
            curActivity = new ActActivity();
            curActivity.setPopUpFlag(0);
            curActivity.setFloatingWindowFlag(0);
            curActivity.setBannerFlag(0);
            curActivity.setLdxTaskFlag(0);
            curActivity.setLsTaskFlag(0);
            curActivity.setVideoTaskFlag(0);
            curActivity.setShareTaskFlag(0);
        }
        return curActivity;
    }


    /**
     * 完成活动任务
     *
     * @param commomParams
     * @param type
     * @param activityId
     */
    @Override
    public ActivityDrawProductVo finishActivityTask(CommonParamsVo commomParams, Integer type, Long activityId) {
        ActActivity activityInfo = this.getActivityInfo(commomParams);
        String key = "";
        String deviceId = commomParams.getDeviceId();
        String drawNumKey = RedisKeyConstant.ACTIVITY_USER_DRAW_NUM + activityId + ":" + deviceId;
        if (type == Constant.activity_task_sign_in) {
            key = RedisKeyConstant.ACTIVITY_TASK_SIGN_IN + activityId + ":" + deviceId;
            Date date = new Date();
            String todayDateStr = tool.util.DateUtil.dateStr2(date);//今天
            if (redisService.exits(key)) {
                String dateStr = redisService.get(key);
                if (todayDateStr.equals(dateStr)) {
                    throw new CommonException(888, "今日已签到");
                }
            }
            Date yesterday = DateUtil.getDateBefore(-1, date);
            String yesDateStr = tool.util.DateUtil.dateStr2(yesterday);//昨天
            Map<String, Object> param = new HashMap<>();
            param.put("activityId", activityId);
            param.put("deviceId", deviceId);
            param.put("signTime", yesDateStr);
            ActSignRecordVo actSignRecordVo = actSignRecordMapper.queryActSignRecord(param);
            if (actSignRecordVo == null) {
                actSignRecordVo = new ActSignRecordVo();
                actSignRecordVo.setDay(1);
            } else {
                int day = actSignRecordVo.getDay();
                actSignRecordVo = new ActSignRecordVo();
                day = day < 7 ? day + 1 : 1;
                actSignRecordVo.setDay(day);
            }
            actSignRecordVo.setActivityId(activityId);
            actSignRecordVo.setDeviceId(deviceId);
            actSignRecordVo.setSignTime(todayDateStr);
            //保存签到信息
            actSignRecordMapper.saveActSignRecord(actSignRecordVo);
            redisService.set(key, todayDateStr, DateUtil.caluRedisExpiredTime());
        } else if (type == Constant.activity_task_ldx) {
            if (!redisService.exits(drawNumKey)) {
                log.info("当前用户未进入过活动页面");
                return null;
            }
            key = RedisKeyConstant.ACTIVITY_TASK_SETTING_LDX + activityId + ":" + deviceId;
            if (redisService.exits(key)) {
                throw new CommonException(888, "今日已成功设置来电秀");
            }
            if (activityInfo.getLdxTaskFlag() == 0) {
                throw new CommonException(777, "任务-成功设置来电秀禁用");
            }
            redisService.set(key, "1", DateUtil.caluRedisExpiredTime());
            redisService.set(drawNumKey, Integer.parseInt(redisService.get(drawNumKey)) + 1, DateUtil.caluRedisExpiredTime());
        } else if (type == Constant.activity_task_ls) {
            if (!redisService.exits(drawNumKey)) {
                log.info("当前用户未进入过活动页面");
                return null;
            }
            key = RedisKeyConstant.ACTIVITY_TASK_SETTING_LS + activityId + ":" + deviceId;
            if (redisService.exits(key)) {
                throw new CommonException(888, "今日已成功设置来电铃声");
            }
            if (activityInfo.getLsTaskFlag() == 0) {
                throw new CommonException(777, "任务-成功设置来铃声禁用");
            }
            redisService.set(key, "1", DateUtil.caluRedisExpiredTime());
            redisService.set(drawNumKey, Integer.parseInt(redisService.get(drawNumKey)) + 1, DateUtil.caluRedisExpiredTime());
        } else if (type == Constant.activity_task_share) {
            if (!redisService.exits(drawNumKey)) {
                log.info("当前用户未进入过活动页面");
                return null;
            }
            key = RedisKeyConstant.ACTIVITY_TASK_SHARE + activityId + ":" + deviceId;
            if (redisService.exits(key)) {
                throw new CommonException(888, "今日已成功分享活动");
            }
            if (activityInfo.getLsTaskFlag() == 0) {
                throw new CommonException(777, "任务-分享活动禁用");
            }
            redisService.set(key, "1", DateUtil.caluRedisExpiredTime());
            redisService.set(drawNumKey, Integer.parseInt(redisService.get(drawNumKey)) + 1, DateUtil.caluRedisExpiredTime());
        } else if (type == Constant.activity_task_video) {
            if (activityInfo.getVideoTaskFlag() == 0) {
                throw new CommonException(777, "任务-观看视频禁用");
            }
            //每日任务：观看视频新增每日观看次数限制，单日单个设备ID限制50次观看消息；
            String watchVideoKey = RedisKeyConstant.ACTIVITY_TASK_VIDEO + activityId + ":" + deviceId;
            int parseInt = 0;
            if (redisService.exits(watchVideoKey)) {
                parseInt = Integer.parseInt(redisService.get(watchVideoKey));
                if (parseInt < 50) {
                    redisService.set(watchVideoKey, parseInt + 1, DateUtil.caluRedisExpiredTime());
                }
            } else {
                redisService.set(watchVideoKey, 1, DateUtil.caluRedisExpiredTime());
            }
            if (parseInt < 50) {
                redisService.set(drawNumKey, Integer.parseInt(redisService.get(drawNumKey)) + 1, DateUtil.caluRedisExpiredTime());
            }
        }
        if (type == Constant.activity_task_sign_in) {
            ActivityDrawProductVo drawProductVo = activityDraw(commomParams, activityId, 2);
            return drawProductVo;
        }
        return null;
    }

    /**
     * 转盘抽奖
     *
     * @param commomParams
     * @param activityId
     * @param source       来源 1转盘抽奖 2任务签到
     * @return
     */
    @Override
    public ActivityDrawProductVo activityDraw(CommonParamsVo commomParams, Long activityId, Integer source) {
        List<Long> configIds = new ArrayList<>();
        if (source == 1) {
            String key = RedisKeyConstant.ACTIVITY_USER_DRAW_NUM + activityId + ":" + commomParams.getDeviceId();
            Integer num = redisService.exits(key) ? Integer.parseInt(redisService.get(key)) : 0;
            if (num <= 0) {
                throw new CommonException(888, "抽奖次数已用光");
            }
            redisService.set(key, num - 1, DateUtil.caluRedisExpiredTime());
            ActActivity actActivity = actActivityMapper.selectByPrimaryKey(activityId);
            actActivity.setDrawNum(actActivity.getDrawNum() + 1);
            actActivityMapper.updateByPrimaryKeySelective(actActivity);
        }
        List<ActActivityConfig> activityConfigs = activityConfigService.getActConfigByActId(activityId);
        //获取用户碎片数量
        List<ActUserDrawRecord> userDrawRecords = userDrawRecordService.getUserDarwRecord(commomParams.getDeviceId(), activityId, null);
        Map<Long, List<ActUserDrawRecord>> drMap = userDrawRecords.stream().collect(Collectors.groupingBy(s -> s.getActivityConfigId()));
        //获取用户兑奖记录
        List<ActUserPrizeExchangeRecord> prizeExchangeRecords = actUserPrizeExchangeRecordService.getUserExchangeRecord(commomParams.getDeviceId(), activityId);
        Map<Long, List<ActUserPrizeExchangeRecord>> erMap = prizeExchangeRecords.stream().collect(Collectors.groupingBy(s -> s.getActivityConfigId()));
        for (ActActivityConfig activityConfig : activityConfigs) {
            Long configId = activityConfig.getId();
            Integer rate = activityConfig.getDebrisDrawRate();
            //签到过来的，过滤掉再来一次
            if (source == 2 && activityConfig.getSort() == 0) {
                continue;
            }
            //奖品是否已领取光了
            if (activityConfig.getPrizeReciveNum() >= activityConfig.getPrizeRealNum() && activityConfig.getSort() != 0) {
                continue;
            }
            //判断是否兑奖过
            if (erMap.containsKey(configId)) {
                continue;
            }
            //未兑奖品,但已经达到兑换条件
            if (drMap.containsKey(configId) && drMap.get(configId).size() == activityConfig.getDebrisReachNum()) {
                continue;
            }
            //判断是否是最后一个碎片(该奖品有10个碎片，用户已得到9个,概率改变)
            if (drMap.containsKey(configId) && drMap.get(configId).size() >= activityConfig.getDebrisReachNum() - 1) {
                rate = activityConfig.getLastDebrisDrawRate();
            }
            for (int i = 0; i < rate; i++) {
                configIds.add(configId);
            }
        }
        if (configIds.size() == 0) {
            throw new CommonException(618, "奖品抽完啦！下次再来哦！");
        }
        Long index = configIds.get(RandomUtil.getRandomRange(configIds.size(), 0));
        ActActivityConfig activityConfig = activityConfigs.stream().filter(s -> s.getId() == index).collect(Collectors.toList()).get(0);
        ActivityDrawProductVo drawProductVo = new ActivityDrawProductVo();
        ActUserDrawRecord record = userDrawRecordService.saveRecord(activityId, activityConfig.getId(), commomParams.getDeviceId());
        if (activityConfig.getSort() != 0) {
            drawProductVo.setDrawRecordId(record.getId());
        }
        BeanUtil.copyProperties(activityConfig, drawProductVo);
        return drawProductVo;
    }

    /**
     * 领取奖品
     *
     * @param commomParams
     * @param darwRecordId
     */
    @Override
    public void receiveAward(CommonParamsVo commomParams, Long darwRecordId) {
        int count = userDrawRecordService.updateRecordState(commomParams.getDeviceId(), darwRecordId);
        if (count <= 0) {
            throw new CommonException(888, "领取失败");
        }
    }

    /**
     * 兑换奖品
     *
     * @param commomParams
     * @param activityConfigId
     * @param contastInfo
     * @param rcvrName
     * @param rcvrAddr
     * @param rcvrPhone
     */
    @Override
    public void exchangeAward(CommonParamsVo commomParams, Long activityConfigId, String contastInfo, String rcvrName, String rcvrAddr, String rcvrPhone) {
        ActActivityConfig activityConfig = actActivityConfigMapper.selectByPrimaryKey(activityConfigId);
        if (activityConfig == null) {
            throw new CommonException(888, "奖品已下架");
        }
        //获取用户碎片数量
        List<ActUserDrawRecord> userDrawRecords = userDrawRecordService.getUserDarwRecord(commomParams.getDeviceId(),
                activityConfig.getActivityId(), activityConfigId);
        if (activityConfig.getDebrisReachNum() > userDrawRecords.size()) {
            throw new CommonException(888, "奖品碎片未集满");
        }
        //获取用户已兑换的数量
        List<ActUserPrizeExchangeRecord> prizeExchangeRecords = actUserPrizeExchangeRecordService
                .getUserExchangeRecord(commomParams.getDeviceId(), activityConfig.getActivityId());
        if (activityConfig.getPrizeRealNum() <= activityConfig.getPrizeReciveNum()) {
            throw new CommonException(888, "没有奖品啦，下次再来吧！");
        }
        boolean b = !prizeExchangeRecords.isEmpty()
                && prizeExchangeRecords.stream().filter(s -> s.getActivityConfigId() == activityConfigId).collect(Collectors.toList()).size() > 0;
        if (b) {
            throw new CommonException(888, "该奖品您已兑换过");
        }
        //更新用户碎片状态为已兑换
        int updateCount = userDrawRecordService.updateUserIsEchangeState(commomParams.getDeviceId(), activityConfigId, activityConfig.getDebrisReachNum());
        if (activityConfig.getDebrisReachNum() > updateCount) {
            throw new CommonException(888, "奖品碎片未集满");
        }
        //添加兑换记录
        int count = actUserPrizeExchangeRecordService.addPrize(activityConfig.getActivityId(), activityConfigId, contastInfo, commomParams, rcvrName, rcvrAddr, rcvrPhone);
        if (count <= 0) {
            throw new CommonException(888, "兑换失败");
        }
        //更新兑换数量
        activityConfig.setPrizeReciveNum(activityConfig.getPrizeReciveNum() + 1);
        actActivityConfigMapper.updateByPrimaryKeySelective(activityConfig);
    }

    /**
     * 获取活动参与人数
     *
     * @param commomParams
     * @return
     */
    @Override
    public Map<String, Object> getActivityJoinNum(CommonParamsVo commomParams, String type) {
        final ActActivity actActivity = this.getCurActivityInfo(commomParams);
        int defaultNum = RandomUtil.getRandomRange(200, 100);
        int num = 616;
        Long curTime = new Date().getTime();
        String key = RedisKeyConstant.ACTIVITY_TOTAL_JOIN_NUM + actActivity.getId();
        String value;
        boolean b = true;//本次是否有需要更新
        Long time = 0L;
        int expiredTime = (int) DateUtil.caluRedisExpiredTime(actActivity.getStartTime(), actActivity.getEndTime());//活动有效期
        if (redisService.exits(key)) {
            value = redisService.get(key);
            String[] vs = value.split("&");
            num = Integer.parseInt(vs[0]);//上次记录的参与人数
            time = Long.parseLong(vs[1]);//上次更新时间点
            //定时调用
            if (Constant.ACTIVITY_JOIN_NUM_TYPE.equals(type)) {
                Long reduct = curTime - time;
                Long ms = 10 * 60 * 1000L;//每10分钟增加100～200人
                if (reduct >= ms) {//计算出倍数
                    int mul = reduct.intValue() / ms.intValue();
                    num = num + (mul * defaultNum);
                } else {
                    b = false;
                }
            }
        }
        if (b) {
            if (Constant.ACTIVITY_JOIN_NUM_TYPE.equals(type)) {
                value = num + "&" + curTime;
            } else {
                String deviceIdKey = RedisKeyConstant.ACTIVITY_TOTAL_JOIN_NUM + actActivity.getId() + ":" + commomParams.getDeviceId();
                if (!redisService.exits(deviceIdKey)) {
                    num++;
                    redisService.set(deviceIdKey, 1, expiredTime);
                }
                value = num + "&" + time;
            }
            redisService.set(key, value, expiredTime);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("joinNum", num);
        return map;
    }

    /**
     * 活动首页滚动播报
     *
     * @return
     */
    @Override
    public List<Map<String, String>> getActivityPageScrollBroadcasts(CommonParamsVo commomParams) {
        final ActActivity actActivity = this.getCurActivityInfo(commomParams);
        //转盘
        List<ActActivityConfig> activityConfigs = activityConfigService.getActConfigByActId(actActivity.getId());
        return this.getScrollBroadcast(activityConfigs);
    }

    public List<ActExchangeRecordVo> queryExchangeRecord(Long activityId,String deviceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("activityId",activityId);
        params.put("deviceId",deviceId);
        return  actUserPrizeExchangeRecordService.queryExchangeRecord(params);
    }

    @Override
    public ActActivity getCurActivity(LdBuryingPointActivityVo ldBuryingPointActivityVo) {
        Map<String, Object> params = new HashMap<>();
        params.put("curDate", ldBuryingPointActivityVo.getCreateDate());
        params.put("appVersion", ldBuryingPointActivityVo.getAppVersion());
        params.put("channelId", ldBuryingPointActivityVo.getChannelId());
        List<ActActivity> actActivitys = actActivityMapper.findActActivity(params);
        if (!actActivitys.isEmpty()) {
            return actActivitys.get(0);
        }
        return null;
    }
}
