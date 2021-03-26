package com.miguan.reportview.controller.common;

import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.dto.HeadFieldDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.service.*;
import com.miguan.reportview.task.ClickhouseHourTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "报表表头字段接口", tags = {"报表表头字段接口"})
@RestController
@Slf4j
public class HeadFieldController {

    @Resource
    private HeadFieldService headFieldService;

    @ApiOperation(value = "根据报表code查询字段信息")
    @PostMapping("/api/head/field/findFieldInfo")
    public ResponseEntity<List<HeadFieldDto>> syncRpTotalHour(@ApiParam(value = "报表code(video_user_keep=视频_留存,video_user_content=视频_用户内容运营数据," +
            "video_realtime=视频_实时统计,video_overall_trend=视频_整体趋势,video_channel=视频_渠道数据,video_ad_son_behavior=视频_子广告行为," +
            "video_ad_error=视频_广告错误数,video_ad_behavior=视频_广告行为,ld_user_keep=来电_留存,ld_user_content=来电_用户内容运营数据,ld_realtime=来电_实时统计," +
            "ld_overall_trend=来电_整体趋势,ld_channel=来电_渠道数据,ld_ad_error=来电_广告错误数,ld_ad_behavior=来电_广告行为,ld_ad_son_behavior=来电_子广告行为," +
            "video_channel_detail=视频_用户明细,video_detail=视频_视频明细,video_section=视频_视频明细曝光区间)", required = true) String tableCode) {
        List<HeadFieldDto> list = headFieldService.findByTableCode(tableCode);
        return ResponseEntity.success(list);
    }

    @ApiOperation(value = "修改字段的显示和隐藏")
    @PostMapping("/api/head/field/modifyFieldShow")
    public void modifyFieldShow(@ApiParam(value = "list字符串(包含id和isShow字段值，多个的时候逗号分隔),格式为id@isShow,例如：20@-1,21@1")  String list) {
        headFieldService.modifyFieldShow(list);
    }

    @PostMapping("/api/head/field/changeHeadFieldInfo")
    public void changeHeadFieldInfo(@ApiParam("操作类型，0=新增，1=修改，2=删除")int type,Integer id, String tableCode, String tableName, String fieldCode, String fieldName, Integer fieldType,
                                    Integer showType, String fieldRemark, Integer isShow, Integer sort) {
        headFieldService.changeHeadFieldInfo(type, id, tableCode, tableName, fieldCode, fieldName, fieldType, showType, fieldRemark, isShow, sort);
    }
}
