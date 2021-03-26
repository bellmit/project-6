package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.AmpFiled;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.common.utils.ReportCommonUtil;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.service.ContentQualityService;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.IChannelDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;


@Api(value = "内容质量评估（魔方后台）", tags = {"内容质量评估（魔方后台）"})
@RestController
public class ContentQualityController extends BaseController {

    @Autowired
    private ContentQualityService contentQualityService;

    @ApiOperation(value = "线上短视频库列表")
    @PostMapping("/api/contentquality/listOnlineVideo")
    public ResponseEntity<PageInfo<OnlineVideoDto>> listOnlineVideo(
            @ApiParam(value = "视频id,多个逗号分隔") String video_id,
            @ApiParam(value = "视频标题") String video_title,
            @ApiParam(value = "内容来源") String video_source,
            @ApiParam(value = "应用（包名）") String package_name,
            @ApiParam(value = "分类")  Long cat_id,
            @ApiParam(value = "合集id") Long gather_id,
            @ApiParam(value = "播放效果:1-标题党，降权处理,2-视频太长需剪辑(跟进时长),3-标题或封面需优化,4-标题/封面/时长优化，关注曝光,5-质量低下，降权处理,6-优质内容,7-不处理，留作观察,8-标题或封面优化，且观察")
                    Integer play_effect,
            @ApiParam(value = "敏感词 -1非敏感词，1:1级敏感词") Integer sensitive,
            @ApiParam(value = "内容修改时间（开始），格式：2020-09-15") String startUpdateDate,
            @ApiParam(value = "内容修改时间（结束），格式：2020-09-15") String endUpdateDate,
            @ApiParam(value = "上线日期（开始），格式：2020-09-15") String startOnlineDate,
            @ApiParam(value = "上线日期（结束），格式：2020-09-15") String endOnlineDate,
            @ApiParam(value = "数据统计日期（开始），格式：2020-09-15") String startStaDate,
            @ApiParam(value = "数据统计日期（结束），格式：2020-09-15") String endStaDate,
            @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：showCount asc(曝光数升序), showcount desc(曝光数降序)。排序字段和返回字段相同")
                    String orderByField,
            @ApiParam(value = "页码", required = true) int pageNum,
            @ApiParam(value = "每页记录数", required = true) int pageSize) {
        if(StringUtils.isNotBlank(orderByField)) {
            orderByField = ReportCommonUtil.humpToLine(orderByField);  //驼峰转下划线
            orderByField = "order by " + orderByField;
        }
        PageInfo<OnlineVideoDto> pageList = contentQualityService.listOnlineVideo(video_id, video_title, video_source, package_name, cat_id, gather_id,play_effect,sensitive, startUpdateDate,
                endUpdateDate, startOnlineDate, endOnlineDate, startStaDate, endStaDate, orderByField, pageNum, pageSize);
        return success(pageList);
    }

    @ApiOperation(value = "线上短视频-数据对比")
    @PostMapping("/api/contentquality/listOnlineDataContrast")
    public ResponseEntity<PageInfo<OnlineDataContrastDto>> listOnlineDataContrast(
            @ApiParam(value = "视频id", required = true) Long video_id,
            @ApiParam(value = "数据统计日期（开始），格式：2020-09-15") String startStaDate,
            @ApiParam(value = "数据统计日期（结束），格式：2020-09-15") String endStaDate,
            @ApiParam(value = "页码", required = true) int pageNum,
            @ApiParam(value = "每页记录数", required = true) int pageSize) {
        PageInfo<OnlineDataContrastDto> pageList = contentQualityService.listOnlineDataContrast(video_id, startStaDate, endStaDate, pageNum, pageSize);
        return success(pageList);
    }

    @ApiOperation(value = "内容质量数据报表")
    @PostMapping("/api/contentquality/listContentQuality")
    public ResponseEntity<PageInfo<ContentQualityDto>> listContentQuality(
            @ApiParam(value = "内容来源") String video_source,
            @ApiParam(value = "应用（包名）") String package_name,
            @ApiParam(value = "分类")  Long cat_id,
            @ApiParam(value = "进文时间（开始），格式：2020-09-15") String startOnlineDate,
            @ApiParam(value = "进文时间（结束），格式：2020-09-15") String endOnlineDate,
            @ApiParam(value = "数据统计日期（开始），格式：2020-09-15") String startStaDate,
            @ApiParam(value = "数据统计日期（结束），格式：2020-09-15") String endStaDate,
            @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：showCount asc(曝光数升序), showCount desc(曝光数降序)。排序字段和返回字段相同")
                    String orderByField,
            @ApiParam(value = "页码", required = true) int pageNum,
            @ApiParam(value = "每页记录数", required = true) int pageSize) {
        if(StringUtils.isNotBlank(orderByField)) {
            orderByField = ReportCommonUtil.humpToLine(orderByField);  //驼峰转下划线
            orderByField = "order by " + orderByField;
        }
        if(StringUtils.isBlank(startStaDate) || StringUtils.isBlank(endStaDate)) {
            startStaDate = "1980-01-01";
            endStaDate = "2100-12-31";
        }
        PageInfo<ContentQualityDto> pageList = contentQualityService.listContentQuality(video_source, package_name, cat_id, startOnlineDate, endOnlineDate,
                startStaDate, endStaDate, orderByField, pageNum, pageSize);
        return success(pageList);
    }
}
