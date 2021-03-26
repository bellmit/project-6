package com.miguan.reportview.service.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.reportview.common.enmus.LdPushUserEnmu;
import com.miguan.reportview.dto.PushVideoResultDto;
import com.miguan.reportview.mapper.PushLdMapper;
import com.miguan.reportview.service.PushLdService;
import com.miguan.reportview.vo.PushVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 来电自动推送
 */
@Service
@Slf4j
public class PushLdServiceImpl implements PushLdService {

    @Resource
    private PushLdMapper pushLdMapper;

    //来电自动推送上线日期(此版本开始，权限埋点被拆分成多个细权限，所以要根据时间来做前后判断)
    private String onlineDate = "2020-10-30";

    /**
     * 修改来电秀是否为视频库
     * @param isPush 是否为push视频库。1是。0否
     * @param videoIds
     */
    public void modifyIsPushTag(Integer isPush, String videoIds) {
        if(StringUtils.isBlank(videoIds)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("isPush", isPush);
        params.put("videoIds", Arrays.asList(videoIds.split(",")));
        pushLdMapper.modifyIsPushTag(params);
    }

    /**
     * 同步push来电秀库的播放数（同步到mysql库）
     */
    public void syncPushLdPlayCount() {
        log.info("同步push来电秀的播放数到mysql(start)");
        try {
            pushLdMapper.deletePushLdMid();  //删除中间表的数据

            int count = pushLdMapper.countPushLd();  //总记录数
            int index = 0;
            int pageSize = 2000;
            Map<String, Object> params = new HashMap<>();
            params.put("pageSize", pageSize);
            while (index < count) {
                params.put("startRow", index);
                List<PushVideoVo> list = pushLdMapper.listPushLd(params);   //在ck中查询出来电秀的播放数

                pushLdMapper.batchSavePushLdMid(list);  //把视频的播放数同步到mysql的中间表中
                index = index + pageSize;
                log.info("同步push视频库数据到中间表，记录：{}", index);
            }
            pushLdMapper.updatePushLdPlayCount();  //把push视频库中间表的数据更新到push视频表中
        } catch (Exception e) {
            log.error("同步push来电秀的播放数到mysql异常", e);
        }
        log.info("同步push来电秀的到mysql(end)");
    }

    public void syncAutoLdPushLog(String arrayList) {
        if(StringUtils.isBlank(arrayList)) {
            log.error("同步来电推送日志记录失败");
            return;
        }
        String[] array = arrayList.split(";");
        List<Map<String, Object>> list = new ArrayList<>();
        for(int i=0;i<array.length;i++) {
            try {
                String[] one = array[i].split(":");
                Map<String, Object> map = new HashMap<>();
                map.put("distinct_id", one[0]);
                int videoId = 0;
                if(StringUtils.isNotBlank(one[1]) && !"null".equals(one[1])) {
                    videoId = Integer.parseInt(one[1]);
                }
                map.put("video_id", videoId);
                String packageName = one[2];
                if("xld".equals(packageName)) {
                    packageName = "com.mg.phonecall";
                }
                map.put("package_name", packageName);
                list.add(map);
            } catch (Exception e) {
                continue;
            }
        }
        pushLdMapper.syncAutoLdPushLog(list);
    }

    /**
     * 同步来电秀分类
     */
    public void syncLdVideoCat() {
        List<Map<String, Object>> list = pushLdMapper.findLdVideosCatList();
        pushLdMapper.deleteCkLdVideoCat();
        pushLdMapper.syncLdVideoCat(list);
    }

    /**
     *  统计（内容推送-新增用户）的数据
     * @param packageName  包名
     * @param triggerType  触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     */
    public void syncNewUser(String packageName, Integer triggerType) {
        log.info(" 统计（内容推送-新增用户）的数据(start)");
        Map<String, Object> params = new HashMap<>();
        List<String> dates = getDates(triggerType);
        List<Integer> dts = getDts(triggerType);
        params.put("dates", dates);
        params.put("dd", dates.get(0));
        params.put("dts", dts);
        params.put("dt", dts.get(0));
        params.put("packageName", packageName);

        //统计（内容推送-新增用户-未设置来电秀-未开通权限-已浏览来电秀）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_A.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserNoSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-新增用户-未设置来电秀-未开通权限-未浏览来电秀）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_B.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserNoSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-新增用户-未设置来电秀-已开通权限-已浏览来电秀）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_C.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserNoSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-新增用户-未设置来电秀-已开通权限-未浏览来电秀）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_D.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserNoSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_E.getCode()));
        //统计（内容推送-新增用户-已设置来电秀-未设置壁纸）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_E.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-新增用户-已设置来电秀-未设置锁屏）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NEWUSER_F.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserSetVideo(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_G.getCode()));
        //统计（内容推送-新增用户-未设置铃声-已浏览铃声）用户
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUserNoSetRing(params);
        log.info(" 统计（内容推送-新增用户）的数据(end)");
    }

    /**
     * 统计活跃用户数据
     * @param packageName 包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param newActivityStartDays 最近一次活跃，开始天数
     * @param newActivityEndDays 最近一次活跃，结束天数
     */
    public void syncActiveUser(String packageName, Integer triggerType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("统计活跃用户数据(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);
        params.put("onlineDate", onlineDate);

        //统计（内容推送-活跃用户-未设置来电秀-未开通权限）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_ACTIVE_A.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveVideos(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-活跃用户-未设置来电秀-已开通权限）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_ACTIVE_B.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveVideos(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_C.getCode()));
        //统计（内容推送-活跃用户-已设置来电秀-未设置壁纸）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_ACTIVE_C.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveVideos(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);
        //统计（内容推送-活跃用户-已设置来电秀-未设置锁屏）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_ACTIVE_D.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveVideos(params);
        pushLdMapper.delLdAutoPushUserVideo(params);
        pushLdMapper.insertLdAutoPushUserVideo(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_E.getCode()));
        //统计（内容推送-活跃用户-未设置铃声）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_ACTIVE_E.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveVideos(params);
        log.info("统计活跃用户数据(end)");
    }

    /**
     * 统计不活跃用户数据
     * @param packageName 包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param notActivityStartDays 不活跃累计，开始天数
     * @param notActivityEndDays 不活跃累计，结束天数
     */
    public void syncNoActiveUser(String packageName, Integer triggerType, Integer notActivityStartDays, Integer notActivityEndDays) {
        log.info("统计不活跃用户数据(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("startDay", notActivityStartDays);
        params.put("endDay", notActivityEndDays);

        //统计（内容推送-不活跃用户-未设置来电秀）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_A.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNoActiveVideos(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_B.getCode()));
        //统计（内容推送-不活跃用户-已设置来电秀-未设置壁纸）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_B.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNoActiveVideos(params);
        //统计（内容推送-不活跃用户-已设置来电秀-未设置锁屏）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_C.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNoActiveVideos(params);

        params.put("filterUserTypeList", getFilterUserType(LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_E.getCode()));
        //统计（内容推送-不活跃用户-未设置铃声-已浏览铃声）用户
        params.put("userType", LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_E.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNoActiveVideos(params);
        log.info("统计不活跃用户数据(end)");
    }


    /**
     * 签到推送-新用户-未签到
     * @param packageName  包名
     * @param triggerType 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送
     */
    public void syncNewUserNoSign(String packageName, int triggerType, int userType) {
        log.info("统计签到推送-新用户-未签到(start)");
        Map<String, Object> params = new HashMap<>();
        List<String> dates = getDates(triggerType);
        List<Integer> dts = getDts(triggerType);
        params.put("dates", dates);
        params.put("dd", dates.get(0));
        params.put("dts", dts);
        params.put("dt", dts.get(0));
        params.put("packageName", packageName);
        params.put("endTime", DateUtil.dateStr(new Date(),"yyyy-MM-dd HH") + ":00:00");
        Date lastHour = DateUtil.rollMinute(new Date(), -60);  //上小时时间
        params.put("startTime", DateUtil.dateStr(lastHour,"yyyy-MM-dd HH") + ":00:00");
        params.put("userType", userType);
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertNewUserNoSign(params);
        log.info("统计签到推送-新用户-未签到(end)");
    }

    /**
     * 签到推送-活跃用户-连续签到
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    public void syncActiveContinueSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("统计签到推送-活跃用户-连续签到(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("userType", userType);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);
        if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_A.getCode()) {
            //连续签到6天
            params.put("day", 6);
        } else if(userType == LdPushUserEnmu.SIGN_PUSH_ACTIVE_B.getCode()) {
            //连续签到2天
            params.put("day", 2);
        }
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertActiveContinueSign(params);
        log.info("统计签到推送-活跃用户-连续签到(end)");
    }

    /**
     * 签到推送-活跃用户-昨日已签到-当日（0-20点）未签到
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    public void syncYesSignTodayNoSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("统计签到推送-活跃用户-昨日已签到-当日（0-20点）未签到(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("userType", userType);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);
        params.put("startTime", dd + " 00:00:00");
        params.put("endTime", dd + " 20:00:00");

        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertYesSignTodayNoSign(params);
        log.info("统计签到推送-活跃用户-昨日已签到-当日（0-20点）未签到(end)");
    }

    /**
     * 统计签到推送-活跃用户-昨日未签到(
     * @param packageName
     * @param triggerType
     * @param userType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    public void syncYesNoSign(String packageName, int triggerType, int userType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("统计签到推送-活跃用户-昨日未签到(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("userType", userType);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);

        log.info("统计签到推送-活跃用户-昨日未签到：{}", params);
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertYesNoSign(params);
        log.info("统计签到推送-活跃用户-昨日未签到(end)");
    }

    /**
     * 活动推送-新增用户
     * @param packageName
     * @param triggerType
     * @param userType
     */
    public void syncNewUserActivity(String packageName, int triggerType, int userType) {
        log.info("统计活动推送-新增用户(start)");
        Map<String, Object> params = new HashMap<>();
        List<String> dates = getDates(triggerType);
        List<Integer> dts = getDts(triggerType);
        String dd = dates.get(0);
        params.put("dates", dates);
        params.put("dd", dd);
        params.put("dts", dts);
        params.put("dt", dts.get(0));
        params.put("packageName", packageName);
        params.put("userType", userType);
        if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_A.getCode()) {
            //18点前新增的用户（0-18点）
            params.put("startTime", dd + " 00:00:00");
            params.put("endTime", dd + " 18:00:00");
        } else if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_B.getCode()) {
            //18点后新增的用户（18:01-23:59）
            params.put("startTime", dd + " 18:00:01");
            params.put("endTime", dd + " 23:59:59");
        }

        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertNewUserActivity(params);
        log.info("统计活动推送-新增用户(end)");
    }

    /**
     * 活动推送-活跃用户
     * @param packageName
     * @param triggerType
     * @param newActivityStartDays
     * @param newActivityEndDays
     */
    public void syncOldUser(String packageName, int triggerType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("统计活动推送-活跃用户(start)");
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("dd", dd);
        params.put("packageName", packageName);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);

        //活动推送-活跃用户-今日抽奖次数=0
        params.put("userType", LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_B.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertOldUserNoLuckDraw(params);

        //活动推送-活跃用户-抽奖次数不等于0的用户
        params.put("userType", LdPushUserEnmu.ACTIVITY_PUSH_ACTIVE_A.getCode());
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.insertOldUserHavLuckDraw(params);
        log.info("统计活动推送-活跃用户(end)");
    }

    /**
     * 统计推送-新增用户
     * @param packageName  包名
     * @param triggerType  触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送
     */
    public void syncSignNewUser(String packageName, Integer triggerType, Integer userType) {
        log.info("(start)统计不活跃用户数据,userType:{}", userType);
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
        }

        if(userType == LdPushUserEnmu.SIGN_PUSH_NEWUSER_A.getCode()) {
            //签到推送-新增用户-未签到
            String dh = DateUtil.dateStr(DateUtil.rollMinuteBefore(new Date(), 60),"yyyyMMddHH");  //计算上一小时
            params.put("startDh", Integer.parseInt(dh));
            params.put("endDh", Integer.parseInt(dh));
        } else if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_A.getCode()) {
            //活动推送-新增用户-未访问活动页面-18点前新增的用户（0-18点）
            String date = dd.replace("-", "");
            params.put("startDh", Integer.parseInt(date + "00"));
            params.put("endDh", Integer.parseInt(date + "17"));
        } else if(userType == LdPushUserEnmu.ACTIVITY_PUSH_NEWUSER_B.getCode()) {
            //活动推送-新增用户-未访问活动页面-18点后新增的用户（18:01-23:59）
            String date = dd.replace("-", "");
            params.put("startDh", Integer.parseInt(date + "18"));
            params.put("endDh", Integer.parseInt(date + "23"));
        }
        params.put("packageName", packageName);
        params.put("dd", dd);
        params.put("userType", userType);

        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNewUser(params);
        log.info("(end)统计不活跃用户数据,userType:{}", userType);
    }

    /**
     * 统计推送-活跃用户
     */
    public void syncSignActiveUser(String packageName, Integer triggerType, Integer userType, Integer newActivityStartDays, Integer newActivityEndDays) {
        log.info("(start)统计推送-活跃用户,userType:{}", userType);
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
            newActivityStartDays++;
            newActivityEndDays++;
        }
        params.put("packageName", packageName);
        params.put("dd", dd);
        params.put("userType", userType);
        params.put("startDay", 0-newActivityStartDays);
        params.put("endDay", 0-newActivityEndDays);

        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncActiveUser(params);
        log.info("(end)统计推送-活跃用户,userType:{}", userType);
    }

    /**
     * 统计推送-不活跃用户
     */
    public void syncSignNoActiveUser(String packageName, Integer triggerType, Integer userType, Integer notActivityStartDays, Integer notActivityEndDays) {
        log.info("(start)统计推送-不活跃用户,userType:{}", userType);
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if(triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
        }
        params.put("packageName", packageName);
        params.put("dd", dd);
        params.put("userType", userType);
        params.put("startDay", notActivityStartDays);
        params.put("endDay", notActivityEndDays);
        pushLdMapper.deleteLdPushUser(params);
        pushLdMapper.syncNoActiveUser(params);
        log.info("(end)统计推送-不活跃用户,userType:{}", userType);
    }

    /**
     * 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param triggerType
     * @return
     */
    private List<String> getDates(Integer triggerType) {
        List<String> dates = new ArrayList<>();
        if(triggerType == 2) {
            dates.add(DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1)));  //昨天
        }
        dates.add(DateUtil.dateStr2(new Date()));
        return dates;
    }

    /**
     * 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     * @param triggerType
     * @return
     */
    private List<Integer> getDts(Integer triggerType) {
        List<Integer> dts = new ArrayList<>();
        if(triggerType == 2) {
            dts.add(Integer.parseInt(DateUtil.dateStr7(DateUtil.rollDay(new Date(), -1))));  //昨天
        }
        dts.add(Integer.parseInt(DateUtil.dateStr7(new Date())));
        return dts;
    }

    private List<Integer> getFilterUserType(Integer userType) {
        List<Integer> list = new ArrayList<>();
        if(userType == LdPushUserEnmu.CONTENT_PUSH_NEWUSER_E.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_NEWUSER_F.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_NEWUSER_G.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_A.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_B.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_C.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_D.getCode());
        }
        if(userType == LdPushUserEnmu.CONTENT_PUSH_NEWUSER_G.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_E.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_NEWUSER_F.getCode());
        }
        if(userType == LdPushUserEnmu.CONTENT_PUSH_ACTIVE_C.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_ACTIVE_D.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_ACTIVE_E.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_A.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_B.getCode());
        }
        if(userType == LdPushUserEnmu.CONTENT_PUSH_ACTIVE_E.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_C.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_ACTIVE_D.getCode());
        }

        if(userType == LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_B.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_C.getCode() || userType == LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_E.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_A.getCode());
        }
        if(userType == LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_E.getCode()) {
            list.add(LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_B.getCode());
            list.add(LdPushUserEnmu.CONTENT_PUSH_NOT_ACTIVE_C.getCode());
        }
        if(list.isEmpty()) {
            list = null;
        }
        return list;
    }


    /**
     * 自动推送来电接口
     * @param userType 用户类型
     * @param packageName app包名
     * @param dd 日期：yyyy-MM-dd
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return
     */
    public List<PushVideoResultDto> findLdAutoPushList(Integer userType, String packageName, String dd, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("dd", dd);
        params.put("userType", userType);
        params.put("packageName", packageName);
        int startRow = (pageNum == 1 ? 0 : pageNum * pageSize);
        int endRow = pageSize;
        params.put("startRow", startRow);
        params.put("endRow", endRow);

        List<PushVideoResultDto> pageList = null;
        if(LdPushUserEnmu.getIsNeedVideo(userType)) {
            pageList = pushLdMapper.findLdAutoPushList(params);
        } else {
            pageList = pushLdMapper.findLdAutoPushUserList(params);
        }
        return pageList;
    }

    /**
     *
     * @param type 统计类型，1--来电秀被收藏的次数，2--来电秀被使用次数
     * @param videoId
     * @return
     */
    public Integer countVideoNum(int type, int videoId) {
        if(type == 1) {
            return pushLdMapper.countVideoCollect(videoId);
        } else {
            return pushLdMapper.countVideoTabConfirm(videoId);
        }
    }
}
