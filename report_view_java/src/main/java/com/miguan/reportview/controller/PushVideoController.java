package com.miguan.reportview.controller;

import com.github.pagehelper.PageInfo;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.PushVideoDto;
import com.miguan.reportview.dto.PushVideoResultDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.dto.VideoLeaderboarDto;
import com.miguan.reportview.service.IVideoViewService;
import com.miguan.reportview.service.PushVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "自动推送push视频库", tags = {"自动推送push视频库"})
@RestController
public class PushVideoController extends BaseController {

    @Resource
    private PushVideoService pushVideoService;

    @ApiOperation(value = "查询视频的观看数、完播数")
    @PostMapping("/api/push/video/findPushVideosInfo")
    public ResponseEntity<List<PushVideoDto>> findPushVideosInfo(@ApiParam(value = "视频ID,多个逗号分隔")  String videoIds) {
        List<PushVideoDto> list = pushVideoService.findPushVideosInfo(this.seqArray2List(videoIds));
        return success(list);
    }

    @ApiOperation(value = "新增或删除push视频库")
    @PostMapping("/api/push/video/saveAndDeletePushVideos")
    public void saveAndDeletePushVideos(@ApiParam(value = "类型，1：新增，2：删除")  Integer type,
                                        @ApiParam(value = "视频ID,多个逗号分隔")  String videoIds) {
        pushVideoService.saveAndDeletePushVideos(type, this.seqArray2List(videoIds));
    }


    @ApiOperation(value = "自动推送视频接口")
    @PostMapping("/api/push/video/findAutoPushList")
    public ResponseEntity<List<PushVideoResultDto>> findAutoPushList(@ApiParam(value = "用户类型，1:用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为;" +
            "2:新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为" +
            "3:新用户（激活当天) 当日产生播放行为" +
            "4:老用户（激活次日以后） 当日产生播放行为" +
            "5:老用户（激活次日以后） 当日未产生播放行为" +
            "6:不活跃用户（未启动天数>=30天）") Integer type,
                                                                     @ApiParam(value = "包名") String packageName,
                                                                     @ApiParam(value = "日期,yyyy-MM-dd") String dd,
                                                                     @ApiParam(value = "页码", required = true) Integer pageNum,
                                                                     @ApiParam(value = "每页记录数", required = true) Integer pageSize) {
        List<PushVideoResultDto> list = pushVideoService.findAutoPushList(type,packageName,dd,pageNum, pageSize);
        return success(list);
    }

    @ApiOperation(value = "同步自动推送记录表")
    @PostMapping("/api/push/video/syncAutoPushLog")
    public void syncAutoPushLog( @ApiParam(value = "数组字符串，格式：distinct_id1,distinct_id2,distinct_id3:video_id:package_name;distinct_id1,distinct_id2,distinct_id3:video_id:package_name") String arrayList) {
        pushVideoService.syncAutoPushLog(arrayList);
    }
}
