package com.miguan.reportview.task;

import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.mapper.DwUserSimpleMapper;
import com.miguan.reportview.service.IUserKeepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@Component
public class RpUserKeepTask {

    @Resource
    private DwUserSimpleMapper dwUserSimpleMapper;
    @Resource
    private IUserKeepService userKeepService;

    private static ExecutorService excutorService = newFixedThreadPool(5);

    /**
     * 同步用户昨日留存
     */
    @Scheduled(cron = "${task.scheduled.cron.sync-user-keep}")
    public void syncUserKeepData() {
        log.info("同步用户昨日留存任务开始");
        syncUserKeepService(DateUtil.yyyy_MM_dd(), DateUtil.yedyyyy_MM_dd());
        log.info("同步用户昨日留存任务结束");
    }

    public void syncUserKeepService(String todayStr, String yesterdayStr) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("today", todayStr);
        params.put("yesterday", yesterdayStr);

        Integer today = Integer.parseInt(todayStr.replace("-", ""));
        Integer yesterday = Integer.valueOf(yesterdayStr.replace("-", ""));

        // 昨日留存总记录数
        int count = dwUserSimpleMapper.countYesterDayUserKeep(params);
        log.info("昨日留存总记录数>>{}", count);

        userKeepService.deleteBySd(yesterday, 1);  //删除视频的留存数据

        int index = 0;
        int pageSize = 5000;
        params.put("pageSize", pageSize);
        while (index < count) {
            Map<String, Object> listParams = new HashMap<>();
            listParams.putAll(params);
            listParams.put("startRow", index);
            excutorService.execute(() -> {
                List<Map<String, Object>> dataList = dwUserSimpleMapper.findYesterDayUserKeep(listParams);
                List<RpUserKeep> userKeepList = this.changeToRpUserKeepList(today, yesterday, dataList);
                userKeepService.insertBatch(userKeepList);
            });
            index = index + pageSize;
        }
    }

    private List<RpUserKeep> changeToRpUserKeepList(int today, int yesterday, List<Map<String, Object>> dataList){
        List<RpUserKeep> userKeepList = new ArrayList<RpUserKeep>();
        dataList.forEach(t->{
            userKeepList.add(this.changeToRpUserKeep(today, yesterday, t));
        });
        return userKeepList;
    }

    private RpUserKeep changeToRpUserKeep(int today, int yesterday, Map<String, Object> data){
        String package_name = data.get("package_name") == null ? "-1" : (String) data.get("package_name");
        String app_version = data.get("app_version") == null ? "-1" : (String) data.get("app_version");
        String change_channel = data.get("change_channel") == null ? "-1" : (String) data.get("change_channel");
        String father_channel =data.get("father_channel") == null ? "-1" : (String) data.get("father_channel");
        Integer is_new_app = (Integer) data.get("is_new_app") == -1 ? -1 : (Integer) data.get("is_new_app");
        BigInteger user = (BigInteger) data.get("user");
        BigInteger luser = (BigInteger) data.get("luser");

        RpUserKeep userKeep = new RpUserKeep();
        userKeep.setDd(today);
        userKeep.setPackageName(package_name);
        userKeep.setAppVersion(app_version);
        userKeep.setChangeChannel(change_channel);
        userKeep.setFatherChannel(father_channel);
        userKeep.setIsNew(is_new_app);
        userKeep.setSd(yesterday);
        userKeep.setUser(user.intValue());
        userKeep.setKeep1(luser.intValue());
        return userKeep;
    }

    /**
     * 同步来电用户昨日留存
     */
    @Scheduled(cron = "${task.scheduled.cron.sync-lduser-keep}")
    public void syncLdUserKeepDataRealTime() {
        log.info("同步来电用户昨日留存(start)");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        userKeepService.syncLdUserKeepData(calendar.getTime());
        log.info("同步来电用户昨日留存(end)");
    }

    /**
     * 每天3点同步来电 历史留存率数据（重新统计统计前天，大前天，前3天，前4天。。。。前30天的留存）
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncLdUserKeepData() {
        log.info("开始同步来电历史留存率数据（start）");
        List<Date> dateList = getHistoryDateList();
        for(Date date : dateList) {
            userKeepService.syncLdUserKeepData(date);
        }
        log.info("开始同步来电历史留存率数据（end）");
    }

    private List<Date> getHistoryDateList() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);  //从前2天的日活开始计算
        List<Integer> historyDateIndex = Arrays.asList(1,2,3,4,5,6,7,14,30);
        List<Date> dateList = new ArrayList<>();
        Date sd = calendar.getTime();

        historyDateIndex.forEach(r -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sd);
            cal.add(Calendar.DAY_OF_YEAR, 0-r);
            dateList.add(cal.getTime());
        });
        return dateList;
    }
}
