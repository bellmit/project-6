package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自动推送结果表
 */
@ApiModel("自动推送请求参数格式")
@Data
public class PushDto {

    @ApiModelProperty(value = "用户类型，1:用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为;" +
            "2:新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为" +
            "3:新用户（激活当天) 当日产生播放行为" +
            "4:老用户（激活次日以后） 当日产生播放行为" +
            "5:老用户（激活次日以后） 当日未产生播放行为" +
            "6:不活跃用户（未启动天数>=30天）" +
            "11001:内容推送-新增用户-未设置来电秀-未开通权限-已浏览来电秀," +
            "11002:内容推送-新增用户-未设置来电秀-未开通权限-未浏览来电秀," +
            "11004:内容推送-新增用户-未设置来电秀-已开通权限-已浏览来电秀," +
            "11005:内容推送-新增用户-未设置来电秀-已开通权限-未浏览来电秀," +
            "12001:内容推送-新增用户-已设置来电秀-未设置壁纸," +
            "12002:内容推送-新增用户-已设置来电秀-未设置锁屏," +
            "13001:内容推送-新增用户-未设置铃声-已浏览铃声," +
            "21007:内容推送-活跃用户-未设置来电秀-未开通权限," +
            "21003:内容推送-活跃用户-未设置来电秀-已开通权限," +
            "22001:内容推送-活跃用户-已设置来电秀-未设置壁纸," +
            "22002:内容推送-活跃用户-已设置来电秀-未设置锁屏," +
            "23002:内容推送-活跃用户-未设置铃声," +
            "31005:内容推送-不活跃用户-未设置来电秀," +
            "32001:内容推送-不活跃用户-已设置来电秀-未设置壁纸," +
            "32002:内容推送-不活跃用户-已设置来电秀-未设置锁屏," +
            "33001:内容推送-不活跃用户-未设置铃声-已浏览铃声," +
            "15003:签到推送-新增用户-未签到," +
            "24001:签到推送-活跃用户-连续6天签到," +
            "24002:签到推送-活跃用户-连续2天签到," +
            "25001:签到推送-活跃用户-昨日已签到," +
            "25002:签到推送-活跃用户-昨日未签到," +
            "16001:活动推送-新增用户-未访问活动页面-18点前新增的用户（0-18点）," +
            "16002:活动推送-新增用户-未访问活动页面-18点后新增的用户（18:01-23:59）," +
            "16003:活动推送-活跃用户-抽奖次数不等于0的用户," +
            "16004:活动推送-活跃用户-今日抽奖次数=0," +
            "18001:活动推送-不活跃用户,")
    private Integer type;
    @ApiModelProperty(value = "包名")
    private String packageName;
    @ApiModelProperty(value = "日期,yyyy-MM-dd")
    private String dd;
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;
    @ApiModelProperty(value = "每页记录数", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "项目类型：1 来电项目，2 视频项目", required = true)
    private Integer projectType;
    @ApiModelProperty(value = "推送ID", required = true)
    private Integer pushId;
    @ApiModelProperty(value = "推送消息ID", required = true)
    private String msgId;
}
