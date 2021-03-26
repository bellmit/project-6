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

	//开关状态
	public static final int open = 1;//开启
	public static final int close = 0;//关闭

	//手机类型iOS
	public static final String IOS_MOBILE = "1";
	//锁屏广告
	public static final String LOCK_SCREEN_POSITION = "lockScreenDeblocking";

	public static final String ANDROIDPACKAGE = "com.mg.xyvideo";

	public static final String IOSPACKAGE = "com.mg.westVideo";

	public static final List<String> IOSPACKAGELIST = new ArrayList<String>();
	static{
		IOSPACKAGELIST.add("com.mg.westVideo");
		IOSPACKAGELIST.add("com.xm98.grapefruit");
	}

	public static final String IOS = "1";//手机类型：1-ios
	public static final String ANDROID = "2";//手机类型：2：安卓

	//开关状态
	public static final String OPENSTR = "1";//开启

	public static final String GGSPPACKAGE = "com.mg.ggvideo";
	public static final String MTSPPACKAGE = "com.mg.mtvideo";

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

	public static final int ADV_MAX_NUM = 10;//广告返回个数
    //广告错误日志保存到Mongodb
	public static final String ADVERT_ERROR_COUNT_LOG_MONGODB = "ad_error_count_log";
	public static final String ADVERT_ERROR_LOG_MONGODB = "ad_error";

	//非集合视频
	public static final String GATHERID = "0";

	//广告订单保存到Mongodb
	//下发订单
	public static final String ADVERT_ORDER_MONGODB = "ad_ord";
	//曝光订单
	public static final String ADVERT_ORDER_EXPOSURE_MONGODB = "ad_ord_exp";
	//扣费订单
	public static final String ADVERT_ORDER_EFFECT_MONGODB = "ad_ord_eff";

	public static final String queryType = "queryType";
	public static final String flow = "flow";
	public static final String abTestAdv = "abTestAdv";
	public static final String position = "position";

	//视频类型
	public static final int video_type = 1;
	//来电类型
	public static final int ld_type = 2;
}
