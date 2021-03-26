package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.entity.*;
import com.miguan.laidian.mapper.LdBuryingPointAdditionalMapper;
import com.miguan.laidian.mapper.LdBuryingPointMapper;
import com.miguan.laidian.mapper.LdBuryingUserVideosMapper;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.repositories.LdBuryingPointUserJpaRepository;
import com.miguan.laidian.service.SmallVideoService;
import com.miguan.laidian.service.UserBuriedPointService;

import com.miguan.laidian.vo.LdBuryingPointEveryVo;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserBuriedPointServiceImpl implements UserBuriedPointService {

    @Resource
    LdBuryingPointMapper ldBuryingPointMapper;

    @Resource
    LdBuryingUserVideosMapper ldBuryingUserVideosMapper;

    @Resource
    LdBuryingPointAdditionalMapper ldBuryingPointAdditionalMapper;

    @Resource
    LdBuryingPointUserJpaRepository ldBuryingPointUserJpaRepository;

    @Resource
    SmallVideoService smallVideoService;


    private static final String APP_START = "App_start";

    @Override
    public void userBuriedPointEvery(LdBuryingPointEvery ldBuryingPointEvery) {
        ldBuryingPointEvery.setUserState(getUserState(ldBuryingPointEvery.getDeviceId(), ldBuryingPointEvery.getAppType()));

        LdBuryingPointEveryVo ldBuryingPointEveryVo = new LdBuryingPointEveryVo();
        //复制数据
        BeanUtils.copyProperties(ldBuryingPointEvery,ldBuryingPointEveryVo);
        Map<String, Object> datas = new ConcurrentHashMap<>(100);
        String jsonStr = JSONObject.toJSONString(ldBuryingPointEveryVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.keySet().forEach(e -> datas.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        Date date = new Date();
        String dateToStr = DateUtil.parseDateToStr(date, "yyyyMMdd");
        datas.put("create_time", date);
        datas.put("create_date", DateUtil.parseDateToStr(date,"yyyy-MM-dd"));
        ldBuryingPointMapper.insertDynamic("ld_burying_point_every" + dateToStr,datas);
    }

    @Scheduled(cron = "0 0 16 * * ?")
    public void createTableQuartz() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.CREATE_TABLE_REDIS_LOCK, RedisKeyConstant.CREATE_TABLE_REDIS_LOCK_TIME);
        if (redisLock.lock()) {
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date nextDate = c.getTime();
            String dateToStr = DateUtil.parseDateToStr(nextDate, "yyyyMMdd");
            //创建埋点表
            ldBuryingPointMapper.createTableDynamic("ld_burying_point_every" + dateToStr);
            log.info("ld_burying_point_every" + dateToStr + "表创建成功=======");
        }
    }


    @Override
    public void userBuriedPointSecondEdition(LdBuryingPoint ldBuryingPoint) {
        //获取当前埋点的数据类型，是否是激活判断，如果是插入数据，如果不是则更新最新的一条激活数据，根据设备号
        if (APP_START.equals(ldBuryingPoint.getActionId())) {
            LdBuryingPoint ldBuryingPointASC = ldBuryingPointMapper.selectByDeviceIdAndAppTypeOrderByCreateTimeAsc(ldBuryingPoint.getDeviceId(), ldBuryingPoint.getAppType());
            LdBuryingPoint ldBuryingPointDESC = ldBuryingPointMapper.selectByDeviceIdAndAppTypeOrderByCreateTimeDESC(ldBuryingPoint.getDeviceId(), ldBuryingPoint.getAppType());
            if (ldBuryingPointASC == null) {
                ldBuryingPoint.setOpenTime(new Date());
            } else {
                ldBuryingPoint.setOpenTime(ldBuryingPointASC.getOpenTime());
                ldBuryingPoint.setUpOpenTime(ldBuryingPointDESC.getCreateTime());
            }
            try {
                ldBuryingPointMapper.ldBuryingPoint(ldBuryingPoint);
            } catch (Exception e) {
                if (e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                    log.error("埋点已存在，旧埋点捕获异常，唯一索引重复");
                }
            }
        } else {
            LdBuryingPoint ldBuryingPointDESC = ldBuryingPointMapper.selectByDeviceIdAndAppTypeOrderByCreateTimeDESC(ldBuryingPoint.getDeviceId(), ldBuryingPoint.getAppType());
            if (ldBuryingPointDESC != null) {
                //判断如果之前序列号不包含当前传入序列号，以逗号分隔方式进行拼接 英文,号 ; sql初始化当前字段为空  判断当前是否是空字符串，如果是则不修改SerialNumber
                if (StringUtils.isNotBlank(ldBuryingPointDESC.getSerialNumber())
                        && !ldBuryingPointDESC.getSerialNumber().contains(ldBuryingPoint.getSerialNumber())) {
                    ldBuryingPointDESC.setSerialNumber(ldBuryingPointDESC.getSerialNumber() + "," + ldBuryingPoint.getSerialNumber());
                }
                ldBuryingPointDESC.setActionId(ldBuryingPoint.getActionId());
                ldBuryingPointDESC.setAllPermission(ldBuryingPoint.getAllPermission());
                ldBuryingPointMapper.updateByIdAndTimeDESC(ldBuryingPointDESC);
            }
        }
    }

    @Override
    @Transactional
    public void ldBuryingPointAdditionalSecondEdition(LdBuryingPointAdditional ldBuryingPointAdditional) throws Exception {
        ldBuryingPointAdditional.setSubmitTime(new Date());
        //查询当天是否有数据已经产生，如果没有添加数据，如果有进行更新操作
        LdBuryingPointAdditional todayBuryingPoint = ldBuryingPointAdditionalMapper.findTodayBuryingPoint(ldBuryingPointAdditional);
        //如果没有添加数据，如果有进行更新操作 并且是激活状态
        if (todayBuryingPoint == null && LdBuryingPointAdditional.ACTIVATION_BURYING.equals(ldBuryingPointAdditional.getActionId())) {
            try {
                ldBuryingPointAdditional.setUserState(getUserState(ldBuryingPointAdditional.getDeviceId(), ldBuryingPointAdditional.getAppType()));
                ldBuryingPointAdditionalMapper.insertSelective(ldBuryingPointAdditional);
            } catch (Exception e) {
                if (e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                    log.error("埋点已存在，新埋点捕获异常，唯一索引重复");
                }
            }
        } else {
            //判断当前用户设置来电秀
            if (LdBuryingPointAdditional.SET_THE_CALL.equals(ldBuryingPointAdditional.getActionId())) {
                //赋值
                LdBuryingUserVideos ldBuryingUserVideos = new LdBuryingUserVideos();
                ldBuryingUserVideos.setDeviceId(ldBuryingPointAdditional.getDeviceId());
                ldBuryingUserVideos.setOperationType(LdBuryingUserVideos.INCOMINGCALLVIDEO);
                ldBuryingUserVideos.setVideosId(ldBuryingPointAdditional.getVideosId());
                ldBuryingUserVideos.setAppType(ldBuryingPointAdditional.getAppType());
                ldBuryingUserVideos.setCreateDay(new Date());
                //根据视频id  设备id  当前时间  操作类型  获取当天这个视频  是否被设置成为来电秀    如果有数据代表今天已经被设置来电秀了。就不进行来电秀设置埋点的保存
                //如果没被设置  并且当前设备数据没超过三条，那么当前数据更新表，并且添加用户今天和视频的关系
                //查询当天这个视屏有没有设置过
                LdBuryingUserVideos ldBuryingUserVideos1 = ldBuryingUserVideosMapper.selectByDeviceIdAndVideoIdAndOperationType(ldBuryingUserVideos);
                //查询用户当天设置来电秀的数量
                int ldBuryingUserCount = ldBuryingUserVideosMapper.selectCountByDeviceIdAndVideoIdAndOperationType(ldBuryingUserVideos);
                if (ldBuryingUserVideos1 == null && ldBuryingUserCount < 3) {
                    ldBuryingUserVideosMapper.insertSelective(ldBuryingUserVideos);
                    if (ldBuryingUserCount == 0) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.SET_THE_CALL_1);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    } else if (ldBuryingUserCount == 1) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.SET_THE_CALL_2);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    } else if (ldBuryingUserCount == 2) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.SET_THE_CALL_3);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    }
                }
            } else if (LdBuryingPointAdditional.A_SMALL_VIDEO_INITIALIZATION.equals(ldBuryingPointAdditional.getActionId()) || LdBuryingPointAdditional.A_SMALL_VIDEO_5.equals(ldBuryingPointAdditional.getActionId())) {
                //向下兼容      1.7.0数据
                ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.A_SMALL_VIDEO_INITIALIZATION);
                //赋值
                LdBuryingUserVideos ldBuryingUserVideos = new LdBuryingUserVideos();
                ldBuryingUserVideos.setDeviceId(ldBuryingPointAdditional.getDeviceId());
                ldBuryingUserVideos.setVideosId(ldBuryingPointAdditional.getVideosId());
                ldBuryingUserVideos.setAppType(ldBuryingPointAdditional.getAppType());
                ldBuryingUserVideos.setCreateDay(new Date());
                ldBuryingUserVideos.setOperationType(LdBuryingUserVideos.SMALLVIDEOS);
                //查询当天这个视频有没有浏览过
                LdBuryingUserVideos ldBuryingUserVideos1 = ldBuryingUserVideosMapper.selectByDeviceIdAndVideoIdAndOperationType(ldBuryingUserVideos);
                //查询当天查看视频数量
                int ldBuryingUserCount = ldBuryingUserVideosMapper.selectCountByDeviceIdAndVideoIdAndOperationType(ldBuryingUserVideos);
                //当天这个视频有没有浏览过 且 插入数据小于等于5
                if (ldBuryingUserVideos1 == null && ldBuryingUserCount < 5) {
                    ldBuryingUserVideosMapper.insertSelective(ldBuryingUserVideos);
                    if (ldBuryingUserCount == 0) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.A_SMALL_VIDEO_1);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    } else if (ldBuryingUserCount == 1) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.A_SMALL_VIDEO_2);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    } else if (ldBuryingUserCount == 4) {
                        ldBuryingPointAdditional.setActionId(LdBuryingPointAdditional.A_SMALL_VIDEO_5);
                        ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
                    }
                }
            } else {
                ldBuryingPointAdditionalMapper.updateLdBuryingUserVideosByActionId(ldBuryingPointAdditional);
            }
        }
        if (LdBuryingPointAdditional.A_SMALL_VIDEO_INITIALIZATION.equals(ldBuryingPointAdditional.getActionId())) {
            //记录视频观看数 add shixh1211
            if (ldBuryingPointAdditional.getVideosId() != null && ldBuryingPointAdditional.getVideosId() > 0) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", ldBuryingPointAdditional.getVideosId());
                params.put("opType", "30");
                smallVideoService.updateVideosCount(params);
            }
        }
    }

    //判断是否是新用户
    public boolean isNewUser(LdBuryingPointUser ldBuryingPointUser) {
        if (ldBuryingPointUser == null) {
            return true;
        } else {
            Date date = DateUtil.getSpecifiedDay(new Date(), "yyyy-MM-dd");
            //如果时间相同那么就是新用户，如果不同就是老用户
            if (DateUtil.getSpecifiedDay(ldBuryingPointUser.getCreateTime(), "yyyy-MM-dd").getTime() == date.getTime()) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean getUserState(String deviceId, String appType) {
        //根据AppType和设备号id  查询是否之前存在数据
        LdBuryingPointUser ldBuryingPointUser = ldBuryingPointUserJpaRepository.findUserBuryingPointIsNew(deviceId, appType);
        if (ldBuryingPointUser == null) {
            ldBuryingPointUserJpaRepository.saveldBuryingPointUser(deviceId, appType, new Date());
        }
        if (isNewUser(ldBuryingPointUser)) return true;
        return false;
    }

}
