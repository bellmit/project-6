package com.miguan.reportview.controller;

import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.PushVideoResultDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.service.PushLdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "来电自动推送push视频库", tags = {"来电自动推送push视频库"})
@RestController
public class PushLdController extends BaseController {

    @Resource
    private PushLdService pushLdService;

    @ApiOperation(value = "设置来电秀是否为push视频库")
    @PostMapping("/api/push/ld/modifyIsPushTag")
    public void modifyIsPushTag(@ApiParam(value = "是否移入push视频库。1是。-1否")  Integer isPush,
                                @ApiParam(value = "视频ID,多个逗号分隔")  String videoIds) {
        pushLdService.modifyIsPushTag(isPush, videoIds);
    }

    @ApiOperation(value = "同步来电自动推送记录表")
    @PostMapping("/api/push/video/syncAutoLdPushLog")
    public void syncAutoLdPushLog( @ApiParam(value = "数组字符串，格式：distinct_id:video_id:package_name;distinct_id:video_id:package_name例如：ae1124:112:com.laidian;actw1:456:com.guoguo") String arrayList) {
        pushLdService.syncAutoLdPushLog(arrayList);
    }


    @ApiOperation(value = "自动推送来电接口")
    @PostMapping("/api/push/ld/findLdAutoPushList")
    public ResponseEntity<List<PushVideoResultDto>> findLdAutoPushList(@ApiParam(value = "11001:内容推送-新增用户-未设置来电秀-未开通权限-已浏览来电秀," +
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
            "18001:活动推送-不活跃用户,") Integer type,
                                                                     @ApiParam(value = "包名") String packageName,
                                                                     @ApiParam(value = "日期,yyyy-MM-dd") String dd,
                                                                     @ApiParam(value = "页码", required = true) Integer pageNum,
                                                                     @ApiParam(value = "每页记录数", required = true) Integer pageSize) {
        if(packageName.equals("xld")) {
            packageName = "com.mg.phonecall";
        }
        List<PushVideoResultDto> list = pushLdService.findLdAutoPushList(type,packageName,dd,pageNum, pageSize);
        return success(list);
    }

    @ApiOperation(value = "根据来电秀id，统计来电秀收藏次数 或 来电秀被使用次数")
    @PostMapping("/api/push/ld/countVideoNum")
    public ResponseEntity<Integer> countVideoNum(@ApiParam(value = "统计类型，1--来电秀被收藏的次数，2--来电秀被使用次数") int type,
                                                 @ApiParam(value = "来电秀id") int videoId) {
        return success(pushLdService.countVideoNum(type, videoId));
    }

}
