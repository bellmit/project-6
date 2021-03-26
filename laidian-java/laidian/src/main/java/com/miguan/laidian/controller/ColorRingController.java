package com.miguan.laidian.controller;

import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.ColorRingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Optional;


@Api(value = "彩铃视频controller", tags = {"对接讯飞的彩铃视频接口"})
@RestController
@RequestMapping("/api/ring")
public class ColorRingController {

    @Resource
    private ColorRingService colorRingService;

    @ApiOperation(value = "查询栏目资源，查询栏目下的子栏目信息")
    @PostMapping("/getQCols")
    public ResultMap getQCols(@ApiParam("栏目编号:彩铃栏目ID-318641;视频栏目ID-318633") String colId,
            @ApiParam("运营商类型:1-中国移动;2-中国联通;2-中国联通;") String operator) {
        return colorRingService.getQCols(Optional.ofNullable(colId).orElse(""),
                Optional.ofNullable(operator).orElse(""));
    }

    @ApiOperation(value = "查询栏目下铃音资源")
    @PostMapping("/getQColRes")
    public ResultMap getQColRes(@ApiParam("栏目编号") String colId,
                                @ApiParam("页码") String currentPage,@ApiParam("单页数据数量") String pageSize,
                                @ApiParam("运营商类型:1-中国移动;2-中国联通;2-中国联通;") String operator) {
        return colorRingService.getQColRes(Optional.ofNullable(colId).orElse(""),
                Optional.ofNullable(currentPage).orElse(""),Optional.ofNullable(pageSize).orElse(""),Optional.ofNullable(operator).orElse(""));
    }

    @ApiOperation(value = "查询栏目下视频")
    @PostMapping("/getQColResVr")
    public ResultMap getQColResVr(@ApiParam("栏目编号") String colId,
                                  @ApiParam("页码") String currentPage,@ApiParam("单页数据数量") String pageSize,
                                  @ApiParam("运营商类型:1-中国移动;2-中国联通;2-中国联通;") String operator) {
        return colorRingService.getQColResVr(Optional.ofNullable(colId).orElse(""),
                Optional.ofNullable(currentPage).orElse(""),Optional.ofNullable(pageSize).orElse(""),Optional.ofNullable(operator).orElse(""));
    }

    @ApiOperation(value = "搜索铃音")
    @PostMapping("/getSearch")
        public ResultMap getSearch(@ApiParam("关键词") String keyword, @ApiParam("歌手") String singer, @ApiParam("歌名") String song,
                                   @ApiParam("页码") String currentPage,@ApiParam("单页数据数量") String pageSize,
                                   @ApiParam("运营商类型:1-中国移动;2-中国联通;2-中国联通;") String operator) {
        return colorRingService.getSearch(Optional.ofNullable(keyword).orElse(""),Optional.ofNullable(singer).orElse(""),Optional.ofNullable(song).orElse(""),
                Optional.ofNullable(currentPage).orElse(""),Optional.ofNullable(pageSize).orElse(""),Optional.ofNullable(operator).orElse(""));

    }

    @ApiOperation(value = "搜索视频")
    @PostMapping("/getSearchVr")
    public ResultMap getSearchVr(@ApiParam("关键词") String keyword,
                                 @ApiParam("页码") String currentPage,@ApiParam("单页数据数量") String pageSize,
                                 @ApiParam("运营商类型:1-中国移动;2-中国联通;2-中国联通;") String operator) {
        return colorRingService.getSearchVr(Optional.ofNullable(keyword).orElse(""),
                Optional.ofNullable(currentPage).orElse(""),Optional.ofNullable(pageSize).orElse(""),Optional.ofNullable(operator).orElse(""));
    }

    @ApiOperation(value = "查铃音搜索词")
    @PostMapping("/getQSkw")
    public ResultMap getQSkw(@ApiParam("数据数量") String showSize) {
        return colorRingService.getQSkw(Optional.ofNullable(showSize).orElse(""));
    }

    @ApiOperation(value = "查视频搜索词")
    @PostMapping("/getQSkwVr")
    public ResultMap getQSkwVr(@ApiParam("数据数量") String showSize) {
        return colorRingService.getQSkwVr(Optional.ofNullable(showSize).orElse(""));
    }


}
