package com.miguan.idmapping.service;

/**
 * 短信Service
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-12
 */
public interface ClSmsService {

	/**
	 * 短信验证
	 * @param phone
	 * @param type
	 * @param code
	 * @return
	 */
	int verifySms(String phone, String type, String code);

}
