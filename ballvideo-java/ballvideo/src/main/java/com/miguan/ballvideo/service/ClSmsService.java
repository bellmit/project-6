package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.SmsTplVo;
import com.miguan.ballvideo.vo.SmsVo;

import java.util.List;

/**
 * 短信Service
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-12
 */
public interface ClSmsService {

	/**
	 * 查询与最近一条短信的时间差（秒）
	 * @param phone
	 * @param type
	 * @return
	 */
	long findTimeDifference(String phone, String type);
	
	/**
	 * 根据手机号码、短信验证码类型查询今日可获取次数，防短信轰炸
	 * @param phone
	 * @param type
	 * @return
	 */
	List<SmsVo> countDayTime(String phone, String type);

	/**
	 * 发送短信
	 * @param phone
	 * @param type
	 * @return
	 */
	String sendSms(String phone, String type,String appPackage,String appVersion);

	/**
	 * 根据type,查询当前启用的短信设置的短信模板
	 * @return
	 */
	SmsTplVo querySmsTplInfoByType(String type);

	/**
	 * 短信验证
	 * @param phone
	 * @param type
	 * @param code
	 * @return
	 */
	int verifySms(String phone, String type, String code);

}
