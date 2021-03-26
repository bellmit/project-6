package com.miguan.ballvideo.common.constants;


import java.util.ArrayList;
import java.util.List;

/**
 * 公用常量类定义
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
public class Constant {

	public static final String RESPONSE_CODE = "code";

	public static final String RESPONSE_CODE_MSG = "message";
	
	public static final int CLIENT_EXCEPTION_CODE_VALUE = 998; // 连接异常（除请求超时）
	
	public static final int TIMEOUT_CODE_VALUE = 999; // 请求超时

	public static final int TIMEOUT_CODE_MAX = 5000; // 超时时间（毫秒）

	//开关状态
	public static final int open = 1;//开启
	public static final int close = 0;//关闭

	//手机类型iOS
	public static final String IOS_MOBILE = "1";
	//锁屏广告
	public static final String LOCK_SCREEN_POSITION = "lockScreenDeblocking";

	public static final String ANDROIDPACKAGE = "com.mg.xyvideo";

	public static final String IOSPACKAGE = "com.mg.westVideo";

	public static final String appPackageWeChat = "com.mg.ggxcx";
	public static final String channelIdWeChat = "xysp_xiaocx";

	public static final String QUICKVIDEOPACKAGE = "com.mg.quickvideo";

	public static final List<String> IOSPACKAGELIST = new ArrayList<String>();
	static{
		IOSPACKAGELIST.add("com.mg.westVideo");
		IOSPACKAGELIST.add("com.xm98.grapefruit");
		IOSPACKAGELIST.add("com.mg.quickvideoios");
	}

	public static final String IOS = "1";//手机类型：1-ios
	public static final String ANDROID = "2";//手机类型：2：安卓
	public static final String WeChat = "3";//手机类型:3：微信小程序

	//开关状态
	public static final String OPENSTR = "1";//开启

	public static final String GGSPPACKAGE = "com.mg.ggvideo";
	public static final String MTSPPACKAGE = "com.mg.mtvideo";
    public static final String DQSPPACKAGE = "com.mg.dqvideo";

	public static final String APPVERSION_180 = "1.8.0";//大于V1.8.0版本
	public static final String APPVERSION_210 = "2.1.0";//大于V2.1.0版本
	public static final String APPVERSION_220 = "2.2.0";//大于V2.2.0版本
	public static final String APPVERSION_231 = "2.3.1";//大于V2.3.1版本
	public static final String APPVERSION_240 = "2.4.0";//大于V2.4.0版本
	public static final String APPVERSION_249 = "2.4.9";//大于V2.4.9版本
	public static final String APPVERSION_250 = "2.5.0";//大于V2.5.0版本，则不返回集合其余信息
	public static final String APPVERSION_253 = "2.5.3";//V2.5.3版本
	public static final String APPVERSION_257 = "2.5.7";//V2.5.7版本
	public static final String APPVERSION_258 = "2.5.8";//大于V2.5.8版本
	public static final String APPVERSION_259 = "2.5.9";//大于V2.5.9版本
	public static final String APPVERSION_267 = "2.6.7";//大于V2.6.7版本
	public static final String APPVERSION_276 = "2.7.6";//大于等于V2.7.6版本
    public static final String APPVERSION_2710 = "2.7.10";//大于V2.7.10版本
	public static final String APPVERSION_280 = "2.8.0";//大于等于V2.7.6版本
	public static final String APPVERSION_300 = "3.0.0";//大于V3.0.0版本
	public static final String APPVERSION_303 = "3.0.3";//大于V3.0.3版本
	public static final String APPVERSION_322 = "3.2.19";//大于V3.2.19版本

	public static final int ADV_MAX_NUM = 10;//广告返回个数
    //广告错误日志保存到Mongodb
	public static final String ADVERT_ERROR_COUNT_LOG_MONGODB = "ad_error_count_log";
	public static final String ADVERT_ERROR_LOG_MONGODB = "ad_error";

	//上传app列表保存到Mongodb
	public static final String APPS_INFO_LIST_MONGODB = "apps_info_list";
	public static final String APPS_INFO_COUNT_MONGODB = "apps_info_count";

	//非集合视频
	public static final String GATHERID = "0";

	//首页弹窗，出现时机:每天出现1次
	public static final int POP_TIMING_DAY = 1;
	//首页弹窗，出现时机:期间仅出现1次
	public static final int POP_TIMING_SECTION = 2;
	//首页弹窗，出现时机:每次重新进入出现1次。
	public static final int POP_TIMING_START = 3;


	public static final String calendarAppPackageWeChat = "com.mg.dfwnl";

	public static final String calendarFlag = "w";

	public static final String guoguoFlag = "g";

	public static final String jumpForH5 = "1";

	public static final String queryType = "queryType";
	public static final String flow = "flow";
	public static final String abTestAdv = "abTestAdv";
	public static final String position = "position";

	//首页推荐标识
	public static final String videoCacheList1 = "videoCacheList1";
	//首页非推荐标识
	public static final String videoCacheList2 = "videoCacheList2";
	//详情页列表标识
	public static final String videoCacheList3 = "videoCacheList3";
	//首页推荐标识
	public static final int recommend1 = 1;
	//首页非推荐标识
	public static final int recommend2 = 2;
	//详情页列表标识
	public static final int recommend3 = 3;
	//即时推荐标识
	public static final int recommend4 = 4;
	//正式环境钉钉机器人发送消息地址
	public static final String webhock = "https://oapi.dingtalk.com/robot/send?access_token=679e88bca5db55e29066505f41ad9f5cea9e7cb8cb006f26a0a7f3ddec4d8d31";
	//测试环境钉钉机器人发送消息地址
	public static final String webhockDev = "https://oapi.dingtalk.com/robot/send?access_token=bc5bd0adef3689e7dd1046c5df9b28702b27e4583cf53c87a18fc67bc37dade5";
	//正式环境钉钉机器人code
	public static final String proCode = "6ab2c2ddabc24e2bbde89226c04523f6";
	//测试环境钉钉机器人code
	public static final String devCode = "492a34f0ad7146fdb01878dfe7479e56";
}
