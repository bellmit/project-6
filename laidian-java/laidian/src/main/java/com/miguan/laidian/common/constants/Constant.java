package com.miguan.laidian.common.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 公用常量类定义
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
public interface Constant {

    String RESPONSE_CODE = "code";

    String RESPONSE_CODE_MSG = "message";

    int CLIENT_EXCEPTION_CODE_VALUE = 998; // 连接异常（除请求超时）

    int TIMEOUT_CODE_VALUE = 999; // 请求超时

    //开关状态
    int open = 1;//开启
    int close = 0;//关闭

    //APP类型
    String appXld = "xld";//炫来电
    String appWld = "wld";//微来电

    //是否展示快捷设置来电秀页面开关
    String FAST_SETTING_LAIDIAN = "fast_setting_laidian";

    String Android = "2";
    String IOS = "1";

    //审核通过
    String approvalState = "2";

    //活动任务
    Integer activity_task_sign_in = 1; //签到
    Integer activity_task_ldx = 2; //来电秀
    Integer activity_task_ls = 3; //铃声
    Integer activity_task_video = 4; //观看视频
    Integer activity_task_share = 5; //分享活动

    String ACTIVITY_JOIN_NUM_TYPE = "0";//抽奖参与人数调用类型（手动），区分定时

    //版本号
    String APPVERSION_190 = "1.9.0";//大于V1.9.0版本
    String APPVERSION_250 = "2.5.0";//大于V2.5.0版本
    String APPVERSION_253 = "2.5.3";//大于V2.5.3版本
    String APPVERSION_260 = "2.6.0";//大于V2.6.0版本
    String APPVERSION_277 = "2.7.7";//大于V2.7.7版本
    String APPVERSION_278 = "2.7.8";//大于V2.7.8版本

    List<String> IOSPACKAGELIST = Arrays.asList("com.mg.westVideo","com.xm98.grapefruit","com.mg.quickvideoios");

    //自动推送日志
    String XLD_AUTO_PUSH_LOG = "xld_auto_push_log";

    //自动推送7日文案日志
    String XLD_AUTO_PUSH_TITLE = "xld_auto_push_title";

    String webhock = "https://oapi.dingtalk.com/robot/send?access_token=679e88bca5db55e29066505f41ad9f5cea9e7cb8cb006f26a0a7f3ddec4d8d31";

    String ABTest = "2";//AB实验标识：1-A实验（旧逻辑）,2-B实验（新逻辑）
}
