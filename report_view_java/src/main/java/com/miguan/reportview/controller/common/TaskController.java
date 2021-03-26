package com.miguan.reportview.controller.common;

import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.service.IXyBuryingPointService;
import com.miguan.reportview.task.ClickhouseHourTask;
import com.miguan.reportview.task.ExportAdpositionTask;
import com.miguan.reportview.task.StatVideoAddAndOffline;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author zhongli
 * @date 2020-08-07 
 *
 */
@Api(value = "定时任务接口", tags = {"定时任务接口"})
@RestController
public class TaskController {
    @Autowired
    private ExportAdpositionTask exportAdpositionTask;
    @Autowired
    private StatVideoAddAndOffline statVideoAddAndOffline;
    @Autowired
    private ClickhouseHourTask clickhouseHourTask;
//    @Autowired
//    private IUserKeepService userKeepService;
    @Resource
    private IXyBuryingPointService xyBuryingPointService;

    private String predate;

    @GetMapping("api/task/exprot/adspace")
    public void exportAdspace() {
        exportAdpositionTask.exportAdposition();
    }

    @GetMapping("api/task/exprot/stavideo/addAndOffline")
    public void exportAdspace(String date) {
        statVideoAddAndOffline.sta(date);
    }


    @GetMapping("api/task/get/sta/time")
    public String getStaTime(String date) {
       return clickhouseHourTask.getPreDate();
    }


    @GetMapping("api/task/sta/hour")
    public void staHour(String date, @RequestParam(value = "force", required = false) Boolean force) {
        LocalDateTime time = LocalDateTime.parse(date, DateUtil.YYYYMMDDHH_FORMATTER);
        if (time.isAfter(LocalDateTime.now())) {
            throw new NullParameterException(() -> "时间不能大于当前时间，无法进行统计");
        }
        if ((force == null || !force.booleanValue()) && date.equals(predate)) {
            throw new NullParameterException(() -> "时间与上一次的相同，避免误重复操作。如果一定要统计要加参数force=true");
        }
        predate = date;
        clickhouseHourTask.doWork(time);
    }

//    @GetMapping("api/task/d/keep")
//    public void deleteKeep(int sd) {
//        userKeepService.delete(sd);
//    }

    @PostMapping("/api/xy/burying/test")
    public void xyBuryingTest(String dateTime){
        xyBuryingPointService.copyToClickHouseFromMongo(LocalDateTime.parse(dateTime, DateUtil.YYYY_MM_DDBHHMMSS_FORMATTER));
    }
}
