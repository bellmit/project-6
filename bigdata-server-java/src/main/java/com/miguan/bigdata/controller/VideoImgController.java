package com.miguan.bigdata.controller;

import com.miguan.bigdata.dto.SimilarParamDto;
import com.miguan.bigdata.dto.SimilarVideoDto;
import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.service.VideoImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "视频标题和背景图片相似度查询接口", tags = {"视频标题和背景图片相似度查询接口"})
@RestController
public class VideoImgController {

    @Resource(name="VideoImgServiceImpl")
    private VideoImgService videoImgService;
    @Resource(name="VideoImgServiceImpl1")
    private VideoImgService videoImgService1;

    @ApiOperation(value = "根据视频标题、视频背景图片相似度查询")
    @PostMapping("/api/similar/findSimVideoList")
    public List<SimilarVideoDto> findSimVideoList(@RequestBody SimilarParamDto paramDto) {
        return videoImgService.findSimVideoList(paramDto);
    }


    @ApiOperation(value = "生成历史视频的背景图片的向量特征")
    @PostMapping("/api/similar/createHistoryImageVector")
    public void createHistoryImageVector(Integer startRow, String videoIds) {
        startRow = (startRow == null ? 0 : startRow);
        videoImgService.createHistoryImageVector(startRow, videoIds);
    }

    @ApiOperation(value = "查询出当前视频库中标题和封面图片有重复可能性的视频")
    @PostMapping("/api/similar/repeatHistoryVideo")
    public void repeatHistoryVideo(Integer startRow,
                                   @ApiParam("类型，0--根据标题和图像，1--只根据图像，2--只根据标题") Integer type,
                                   @ApiParam("视频id，多个逗号分隔，可以为空") String videoIds,
                                   @ApiParam("标题相似度阈值(取值范围：0至1)") Double titleThreshold,
                                   @ApiParam("图片相似度阈值(取值范围：0至1)") Double imgThreshold) {
        startRow = (startRow == null ? 0 : startRow);
        videoImgService1.repeatHistoryVideo(type, startRow, videoIds, titleThreshold, imgThreshold);
    }

    @PostMapping("/api/similar/syncImgVector")
    public void syncImgVector(Integer from, Integer size) {
        videoImgService1.syncImgVector(from, size);
    }
}
