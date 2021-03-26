package com.miguan.laidian.controller;

import com.miguan.laidian.common.util.POIUtil;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(value = "日历controller", tags = {"日历接口"})
@RequestMapping(value = "/api/calendar")
@RestController
public class CalendarController {

    @Resource
    CalendarService calendarService;

    private static final String columns[] = {"day", "week", "solarTerms", "gregorianFestival", "lunarFestival", "specialFestival"};

    /**
     * 模板目录\laidian-java\doc\日历导入模板
     * 1970-2100年万年历数据库 下载地址：www.zhaoxugeng.cn/blog/download/?type=detail&id=1
     *
     * @param opinionImageFile
     * @return
     */
    @ApiOperation(value = "导入Execl添加日历表")
    @PostMapping(value = "/importCalendar")
    public ResultMap getImgUrl(@RequestParam(value = "importCalendar") MultipartFile opinionImageFile) {
        List<Map<String, String>> maps = POIUtil.readExecl(opinionImageFile, columns);
        calendarService.save(maps);
        return ResultMap.success();
    }

    @ApiOperation(value = "获取当天的日期，星期，节日")
    @GetMapping(value = "/queryToday")
    public ResultMap getImgUrl() {
        return ResultMap.success(calendarService.queryCalendarToday());
    }
}
