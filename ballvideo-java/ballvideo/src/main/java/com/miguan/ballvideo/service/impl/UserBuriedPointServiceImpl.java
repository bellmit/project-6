package com.miguan.ballvideo.service.impl;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.UserBuryingPoint;
import com.miguan.ballvideo.entity.UserBuryingPointGarbage;
import com.miguan.ballvideo.entity.UserBuryingPointPush;
import com.miguan.ballvideo.entity.UserBuryingPointUser;
import com.miguan.ballvideo.mapper.BuryingMapper;
import com.miguan.ballvideo.mapper.ClDeviceMapper;
import com.miguan.ballvideo.mapper.ClUserVideosMapper;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.BuryingPointGarbageRepository;
import com.miguan.ballvideo.repositories.BuryingPointPushRepository;
import com.miguan.ballvideo.repositories.UserBuryingPointUserJpaRepository;
import com.miguan.ballvideo.service.FirstVideosOldService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.UserBuriedPointService;
import com.miguan.ballvideo.vo.BuryingActionType;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.GarbageBuryingPointVo;
import com.miguan.ballvideo.vo.queue.UserLabelGradeQueueVo;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserBuriedPointServiceImpl implements UserBuriedPointService {

    @Resource
    private UserBuryingPointUserJpaRepository userBuryingPointUserJpaRepository;

    @Resource
    private FirstVideosOldService firstVideosOldService;

    @Resource
    private BuryingMapper buryingMapper;

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private ClUserVideosMapper clUserVideosMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private BuryingPointPushRepository buryingPointPushRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private BuryingPointGarbageRepository buryingPointGarbageRepository;

    @Resource
    private ClDeviceMapper clDeviceMapper;

    private final String string = "xy_video_play,xy_video_playover,xy_video_praise,xy_video_comment,xy_video_collect,share_way";

    /**
     * xy_burying_point_label表只用于用户标签user_label，每天只保存当天数据
     *
     * @param userBuryingPointVo
     * @return
     */
    @Override
    public ResultMap insert(UserBuryingPointVo userBuryingPointVo) {
        //用户标签类
        boolean canContinue = true;
        String actionId = userBuryingPointVo.getActionId();
        if (string.contains(actionId)) {
            //过滤无用数据
            if (!(BuryingActionType.SHARE_WAY.equals(actionId) && userBuryingPointVo.getSource() == 2)) {
                if (!(BuryingActionType.XY_VIDEO_PLAYOVER.equals(actionId)
                        && userBuryingPointVo.getVideoRate() != null && userBuryingPointVo.getVideoRate() < 80)) {
                    Long catId = userBuryingPointVo.getCatid();
                    Long videoId = userBuryingPointVo.getVideoId();
                    if (catId == null || catId < 1) {
                        if (videoId != null && videoId > 0) {
                            String catIdStr = firstVideosMapper.findCatIdByVideoId(videoId);
                            if (StringUtils.isEmpty(catIdStr)) {
                                log.info("更新用户标签权重分,视频ID=" + videoId + "的分类ID为空！");
                                canContinue = false;
                            }
                            if (StringUtil.isNotBlank(catIdStr) && !"0".equals(catIdStr)) {
                                catId = Long.valueOf(catIdStr);
                                userBuryingPointVo.setCatid(Long.valueOf(catIdStr));
                            }
                        } else {
                            log.info("更新用户标签权重分,视频ID为空！");
                            canContinue = false;
                        }
                    }
                    if (VersionUtil.compareIsHigh(Constant.APPVERSION_300, userBuryingPointVo.getAppVersion()) && canContinue) {
                        try {
                            // 根据设备ID等埋点信息更新用户标签权重分
                            String paramStr = JSON.toJSONString(new UserLabelGradeQueueVo(userBuryingPointVo.getDeviceId(), catId, actionId));
                            rabbitTemplate.convertAndSend(
                                    RabbitMQConstant.USERLABELGRADE_EXCHANGE,
                                    RabbitMQConstant.USERLABELGRADE_KEY, paramStr);
                        } catch (Exception e) {
                            log.info("设备ID：" + userBuryingPointVo.getDeviceId() + "；启动事件标识：" + actionId + "；更新用户标签权重分失败，[{}]" + e.getMessage());
                        }
                    }
                }
            }
        }
        this.updateUserVideosCount(userBuryingPointVo);
        return ResultMap.success();
    }

    @Override
    public Integer judgeUser(String deviceId,String channelId) {
        Date createTime = userBuryingPointUserJpaRepository.findUserBuryingPointIsNew(deviceId);
        //如果查询对象为空，则为老用户。  查询对象创建时间和最新时间是同一天为新用户
        if (createTime == null) {
            //赋值 如果是新用户，往埋点user表中新增
            UserBuryingPointUser userBuryingPointIsNew = new UserBuryingPointUser();
            userBuryingPointIsNew.setDeviceId(deviceId);
            userBuryingPointIsNew.setCreateTime(new Date());
            userBuryingPointIsNew.setChannelId(channelId);
            try {
                userBuryingPointUserJpaRepository.save(userBuryingPointIsNew);
                return UserBuryingPoint.NEW;
            }catch (Exception e){
                log.error("设备ID重复：" + deviceId);
                //前面查询无数据，此时有重复数据，说明有新增，是新用户
                return UserBuryingPoint.NEW;
            }
        }
        if (DateUtil.isTheSameDay(createTime)) {
            return UserBuryingPoint.NEW;
        }
        return UserBuryingPoint.OLD;
    }

    @Override
    @Transactional
    public Integer deleteByDeviceId(String deviceId) {
        //根据deviceId删除BuryingPointUser表数据
        int i = userBuryingPointUserJpaRepository.deleteByDeviceId(deviceId);
        //删除redis缓存数据
        redisService.delByByte(RedisKeyConstant.DEVICEID_KEY + deviceId);
        return i;
    }

    @Override
    public void savePushBuryingPoint(UserBuryingPointPush buryingPointPush) {
        buryingPointPush.setCreateTime(new Date());
        buryingPointPushRepository.save(buryingPointPush);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEntryGarbageBuryingPoint(String jsonString) {
        Map<String, Object> map = JSONObject.parseObject(jsonString);
        // 获取设备表对象
        ClDeviceVo deviceVo = clDeviceMapper.getDeviceByDeviceIdAppPackage(map);
        if (deviceVo == null) {
            log.error("########该设备无关联设备表Id########设备信息：" + jsonString);
            return;
        }
        UserBuryingPointGarbage userBuryingPointGarbage = buryingPointGarbageRepository.findFirstByDeviceId(deviceVo.getId());
        if (userBuryingPointGarbage == null) {
            userBuryingPointGarbage = new UserBuryingPointGarbage();
            userBuryingPointGarbage.setDeviceId(deviceVo.getId());
            userBuryingPointGarbage.setEntryNum(0);
            userBuryingPointGarbage.setCreateDate(new Date());
            userBuryingPointGarbage.setUpdateDate(new Date());
            buryingPointGarbageRepository.save(userBuryingPointGarbage);
            return;
        }
        // 如果清理过垃圾
        if ("1".equals(map.get("type").toString())) {
            userBuryingPointGarbage.setEntryNum(userBuryingPointGarbage.getEntryNum() + 1);
            userBuryingPointGarbage.setUpdateDate(new Date());
            buryingPointGarbageRepository.save(userBuryingPointGarbage);
        }
    }

    /**
     * 组装进入垃圾清理埋点MQ队列数据
     */
    @Override
    public void convertAndSendGarbageBuryingPoint(GarbageBuryingPointVo garbageBuryingPointVo) {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", garbageBuryingPointVo.getDeviceId());
        map.put("appPackage", garbageBuryingPointVo.getAppPackage());
        map.put("type", garbageBuryingPointVo.getType());
        String jsonString = JSONObject.toJSONString(map);
        //往埋点服务burypoint发送推送埋点消息
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_GARBAGE_EXCHANGE, RabbitMQConstant.BURYPOINT_GARBAGE_KEY, jsonString);
    }

//    //恢复未保存数据
//    @Override
//    public ResultMap insertDataLeakage(UserBuryingPointVo userBuryingPointVo) {
//        //恢复数据
//        UserBuryingPoint userBuryingPoint = new UserBuryingPoint();
//        BeanUtils.copyProperties(userBuryingPointVo, userBuryingPoint);
//        //恢复前一天数据
//        if(!this.judgeToday(userBuryingPointVo)){
//            //不是今天，前一天数据
//            Calendar c = Calendar.getInstance();
//            c.add(Calendar.DAY_OF_MONTH, -1);
//            userBuryingPoint.setCreatTime(c.getTime());
//            userBuryingPoint.setCreateDate(DateUtil.parseDateToStr(c.getTime(), "yyyy-MM-dd"));
//            if (StringUtils.isNotEmpty(userBuryingPoint.getDeviceId())) {
//                //获取当前用户是否 是新用户
//                UserBuryingPointUser userBuryingPointIsNew = userBuryingPointUserJpaRepository.findUserBuryingPointIsNew(userBuryingPoint.getDeviceId());
//                //如果查询对象为空，则为老用户。  查询对象创建时间和最新时间是同一天为新用户
//                if (userBuryingPointIsNew == null) {
//                    userBuryingPoint.setIsNew(UserBuryingPoint.NEW);
//                    //赋值 如果是新用户，往埋点user表中新增
//                    UserBuryingPointUser userBuryingPointUser = new UserBuryingPointUser();
//                    userBuryingPointUser.setDeviceId(userBuryingPoint.getDeviceId());
//                    userBuryingPointUser.setCreateTime(new Date());
//                    userBuryingPointUserJpaRepository.save(userBuryingPointUser);
//                } else {
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                    String format = formatter.format(userBuryingPointIsNew.getCreateTime());
//                    if (format.equals(DateUtil.parseDateToStr(c.getTime(), "yyyy-MM-dd"))) {
//                        userBuryingPoint.setIsNew(UserBuryingPoint.NEW);
//                    } else {
//                        userBuryingPoint.setIsNew(UserBuryingPoint.OLD);
//                    }
//                }
//            }
//            this.updateUserVideosCount(userBuryingPoint);
//            //保存埋点
//            userBuryingPointJpaRepository.save(userBuryingPoint);
//            UserBuryingPointDay userBuryingPointDay = new UserBuryingPointDay();
//            UserBuryingPointMonth userBuryingPointMonth = new UserBuryingPointMonth();
//            //2019年11月4日17:28:44  恢复数据  月份
//            BeanUtils.copyProperties(userBuryingPoint, userBuryingPointMonth);
//            userBuryingPointMonthJpaRepository.save(userBuryingPointMonth);
//            //2019年11月4日17:31:17  恢复数据  日期
//            BeanUtils.copyProperties(userBuryingPoint, userBuryingPointDay);
//            userBuryingPointDayJpaRepository.save(userBuryingPointDay);
//            return ResultMap.success();
//        }else {
//            UserBuryingPoint userBuryingPoint1 = this.judgeNewUsers(userBuryingPoint);
//            this.updateUserVideosCount(userBuryingPoint1);
//            userBuryingPointJpaRepository.save(userBuryingPoint);
//            return ResultMap.success();
//        }
//    }

    //修改用户观看视频次数，点赞
    public void updateUserVideosCount(UserBuryingPointVo userBuryingPoint) {
        if(userBuryingPoint.getUserId()==null)return;
        if(userBuryingPoint.getVideoId()==null || userBuryingPoint.getVideoId().intValue()==0)return;//add shixh0323
        final Map<String, Object> datas = new ConcurrentHashMap<>(100);
        if (BuryingActionType.XY_VIDEO_PLAYOVER.equals(userBuryingPoint.getActionId())
                && userBuryingPoint.getVideoId() != null && userBuryingPoint.getVideoId() > 0
                && userBuryingPoint.getVideoRate() != null) {
            if (userBuryingPoint.getVideoRate() >= 80) { //播放完整数
                datas.clear();
                Long videoId = userBuryingPoint.getVideoId();
                datas.put("id", videoId);
                datas.put("opType", "90");
                datas.put("userId", userBuryingPoint.getUserId());
                datas.put("type", "10");
                firstVideosOldService.setRealWeightRedis(videoId);
                firstVideosOldService.updateVideosCountSendMQ(datas);
            } else if (userBuryingPoint.getVideoRate() > 30) { //超过30%时长播放数
                datas.clear();
                Long videoId = userBuryingPoint.getVideoId();
                datas.put("id", videoId);
                datas.put("opType", "100");
                datas.put("userId", userBuryingPoint.getUserId());
                datas.put("type", "10");
                firstVideosOldService.setRealWeightRedis(videoId);
                firstVideosOldService.updateVideosCountSendMQ(datas);
            }
        } else if (BuryingActionType.SHARE_WAY.equals(userBuryingPoint.getActionId())
                && userBuryingPoint.getVideoId() != null && userBuryingPoint.getVideoId() > 0) {
            Long videoId = userBuryingPoint.getVideoId();
            datas.put("id", videoId);
            datas.put("opType", "80");
            datas.put("userId", userBuryingPoint.getUserId());
            datas.put("type", 1 == userBuryingPoint.getSource() ? "10" : "20");
            if (1 == userBuryingPoint.getSource()) {
                firstVideosOldService.setRealWeightRedis(videoId);
            }
            firstVideosOldService.updateVideosCountSendMQ(datas);
        } else if (BuryingActionType.XY_VIDEO_COLLECT.equals(userBuryingPoint.getActionId())
                && userBuryingPoint.getVideoId() != null && userBuryingPoint.getVideoId() > 0) {
            Long videoId = userBuryingPoint.getVideoId();
            firstVideosOldService.setRealWeightRedis(videoId);
        }
    }

    //计算真实权重值
    public int countRealWeight(Long videoId) {
        FirstVideos firstVideos = firstVideosMapper.getFirstVideosById(videoId);
        if (firstVideos != null) {
            int shareCount = Integer.valueOf(firstVideos.getShareCount());//分享数
            int playAllCount = Integer.valueOf(firstVideos.getPlayAllCount());//完整播放数
            int playCount = Integer.valueOf(firstVideos.getPlayCount());//超过30%时长播放数
            int oldRealWeight = Integer.valueOf(firstVideos.getRealWeight());//更新前的真实权重
            //查询视频收藏数
            Map<String, Object> param = new HashMap<>();
            param.put("videoId", videoId);
            param.put("collection", "1");
            param.put("videoType", "10");
            int collectionSum = clUserVideosMapper.findVideoCollectionSum(param);
            int newRealWeight = (int) Math.floor(collectionSum * 3 + (Math.sqrt(playAllCount) + Math.sqrt(playCount)) / 2 + shareCount * 3);
            //如果计算出的新真实权重和旧的真实权重一样，不进行更新
            if (newRealWeight == oldRealWeight) {
                return 0;
            }
            //权重值计算
            return newRealWeight;
        }
        return 0;

    }


/*    //判断当前是否是新用户
    public UserBuryingPointVo judgeNewUsers(UserBuryingPointVo userBuryingPoint){
        userBuryingPoint.setCreateDate(DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd"));
        if (StringUtils.isNotEmpty(userBuryingPoint.getDeviceId())) {
            //获取当前用户是否 是新用户
            UserBuryingPointUser userBuryingPointIsNew = userBuryingPointUserJpaRepository.findUserBuryingPointIsNew(userBuryingPoint.getDeviceId());
            //如果查询对象为空，则为老用户。  查询对象创建时间和最新时间是同一天为新用户
            if (userBuryingPointIsNew == null) {
                userBuryingPoint.setIsNew(UserBuryingPoint.NEW);
                //赋值 如果是新用户，往埋点user表中新增
                UserBuryingPointUser userBuryingPointUser = new UserBuryingPointUser();
                userBuryingPointUser.setDeviceId(userBuryingPoint.getDeviceId());
                userBuryingPointUser.setCreateTime(new Date());
                userBuryingPointUserJpaRepository.save(userBuryingPointUser);
            } else {
                if (DateUtil.parseDateToStr(new Date(),"yyyy-MM-dd").equals(DateUtil.parseDateToStr(userBuryingPointIsNew.getCreateTime(),"yyyy-MM-dd"))) {
                    userBuryingPoint.setIsNew(UserBuryingPoint.NEW);
                } else {
                    userBuryingPoint.setIsNew(UserBuryingPoint.OLD);
                }
            }
        }
        return userBuryingPoint;
    }*/

//    //判断当前时间是否是今天
//    public Boolean judgeToday(UserBuryingPointVo userBuryingPointVo){
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        //判断文件名是否是今天
//        if(userBuryingPointVo.getFileName().split("\\.")[0].equals(dateFormat.format(new Date()).split("-")[2])){
//            return true;
//        }else{
//            return false;
//        }
//    }
}
