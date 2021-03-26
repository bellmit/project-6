package com.miguan.reportview.controller;

import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.dto.VideoLeaderboarDto;
import com.miguan.reportview.service.IVideoViewService;
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

@Api(value = "视频排行", tags = {"视频排行"})
@RestController
public class VideoViewController extends BaseController {

    @Resource
    private IVideoViewService iVideoViewService;

    /**
     * 视频排行
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "视频排行")
    @PostMapping("/api/videoView/leaderboard")
    public ResponseEntity<List<VideoLeaderboarDto>> leaderboard(@ApiParam(value = "排行类型：1 小时， 2 天") Integer type) {
        if (type != 1 && type != 2) {
            return ResponseEntity.error(400, "type参数错误");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        if (type == 1) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String dhStr = localDateTime.minusHours(1).format(DateUtil.YYYYMMDDHH_FORMATTER);
            params.put("dh", Integer.parseInt(dhStr));
        } else {
            params.put("dd", DateUtil.yesyyyyMMdd());
        }
        return success(iVideoViewService.findViewLeaderboard(params));
    }

    /**
     * 查询单个视频的观看数
     *
     * @param videoId
     * @return
     */
    @ApiOperation(value = "单个视频的观看数")
    @PostMapping("/api/videoView/video")
    public ResponseEntity<Map<String, Object>> video(@ApiParam(value = "视频ID") Integer videoId) {
        return success(iVideoViewService.findVideoView(videoId));
    }

}
