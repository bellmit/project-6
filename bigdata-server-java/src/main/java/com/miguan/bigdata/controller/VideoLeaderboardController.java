package com.miguan.bigdata.controller;

import com.miguan.bigdata.common.util.DateUtil;
import com.miguan.bigdata.dto.VideoLeaderboarDto;
import com.miguan.bigdata.service.IVideoLeaderboardService;
import com.miguan.bigdata.vo.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "视频排行", tags = {"视频排行"})
@RestController
@RequestMapping("/api/videoView")
public class VideoLeaderboardController {

    @Resource
    private IVideoLeaderboardService iVideoLeaderboardService;

    /**
     * 视频排行
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "视频排行")
    @PostMapping("/leaderboard")
    public ResultMap<List<VideoLeaderboarDto>> leaderboard(@ApiParam(value = "排行类型：1 小时， 2 天") Integer type) {
        if (type != 1 && type != 2) {
            return ResultMap.error(400, "type参数错误");
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
        return ResultMap.success(iVideoLeaderboardService.findViewLeaderboard(params));
    }

    /**
     * 查询单个视频的观看数
     *
     * @param videoId
     * @return
     */
    @ApiOperation(value = "单个视频的观看数")
    @PostMapping("/video")
    public ResultMap<Map<String, Object>> video(@ApiParam(value = "视频ID") Integer videoId) {
        return ResultMap.success(iVideoLeaderboardService.findVideoView(videoId));
    }

}
