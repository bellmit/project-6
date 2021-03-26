package com.miguan.ballvideo.common.constants;


/**
 * 公用常量类定义
 *
 * @author daoyu
 * @version 1.0.0
 * @date 2020-06-06
 */
public class PushArticleConstant {

	/** 开启 */
	public static final int STATE_OPEN = 1;
	/** 关闭 */
	public static final int STATE_CLOSE = 0;
	/** APP */
	public static final int TYPE_APP = 1;
	/** /链接 */
	public static final int TYPE_LINK = 2;
	/** 首页视频 */
	public static final int TYPE_FIRST_VIDEO = 3;
	/** 小视频 */
	public static final int TYPE_SMALL_VIDEO = 4;
	/** 系统消息 */
	public static final int TYPE_SYSTEM_MSG = 4;
	/** 定时推送 */
	public static final String PUSH_TYPE_CLOCK_TIME = "1";
	/** 立即推送 */
	public static final String PUSH_TYPE_REAL_TIME = "2";
	/** 全部用户推送 */
	public static final String USER_TYPE_ALL_USER = "10";
	/** 指定用户推送 */
	public static final String USER_TYPE_ASSIGN_USER = "20";
	/** 星群人群推送 */
	public static final String USER_TYPE_INTEREST_CAT = "30";
}
