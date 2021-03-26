package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.common.aop.RequestCache;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.LockScreen;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.mapper.VideoMapper;
import com.miguan.laidian.service.CalendarService;
import com.miguan.laidian.service.LockScreenCopywritingService;
import com.miguan.laidian.service.LockScreenService;
import com.miguan.laidian.service.VideoService;
import com.miguan.laidian.vo.CalendarVo;
import com.miguan.laidian.vo.LockHomePageVo;
import com.miguan.laidian.vo.LockScreenCopywritingVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "锁屏controller", tags = {"锁屏接口"})
@RestController
@RequestMapping("/api/lock/screen")
public class LockScreenController {

    @Resource
    private VideoMapper videosMapper;

    @Resource
    private LockScreenService lockScreenService;

    @Resource
    private LockScreenCopywritingService lockScreenCopywritingService;

    @Resource
    private CalendarService calendarService;

    @ApiOperation(value = "随机获取5个壁纸")
    @PostMapping(value = "/getLockScreenList")
    public ResultMap getLockScreenList() {
        List<LockScreen> lockScreenList = lockScreenService.findLockScreenList();
        return ResultMap.success(lockScreenList);
    }

    @ApiOperation(value = "更新壁纸默认设置次数")
    @PostMapping(value = "/updateLockScreen")
    public ResultMap updateLockScreen(Long id) {
        boolean flag = lockScreenService.updateLockScreen(id);
        if (flag) {
            return ResultMap.success();
        }
        return ResultMap.error("更新失败");
    }

    @ApiOperation("获取首页锁屏相关数据")
    @GetMapping("/getLockHomePage")
    public ResultMap getLockHomePage(@ApiParam("排除已浏览文案ID") String excludeIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", Constant.open);
        params.put("excludeIds", excludeIds);
        LockScreenCopywritingVo lockScreenCopywritingVo = lockScreenCopywritingService.getCopywritingInfo(params);
        CalendarVo calendarVo = calendarService.queryCalendarToday();
        LockHomePageVo lockHomePageVo = new LockHomePageVo();
        lockHomePageVo.setCalendarVo(calendarVo);
        lockHomePageVo.setLockScreenCopywriting(lockScreenCopywritingVo);
        return ResultMap.success(lockHomePageVo);
    }

    @ApiOperation(value = "更新锁屏文案点击次数")
    @PostMapping(value = "/updateCopywritingClickNum")
    public ResultMap updateCopywritingClickNum(Long id) {
        boolean flag = lockScreenCopywritingService.updateCopywritingClickNum(id);
        if (flag) {
            return ResultMap.success();
        }
        return ResultMap.error("更新失败");
    }

    @RequestCache
    @ApiOperation("获取首页动态锁屏相关数据")
    @GetMapping("/getDynamicLockHome")
    public ResultMap getDynamicLockHome(Integer currentPage) {
        Map map = new HashMap();
        map.put("videoType","10");
        map.put("recommend", Video.RECOMMEND);
        PageHelper.startPage(currentPage, 8);
        List videosList = videosMapper.findVideosList(map);
        return ResultMap.success(videosList);
    }

}
