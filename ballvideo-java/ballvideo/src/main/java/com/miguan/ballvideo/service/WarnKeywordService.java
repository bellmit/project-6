package com.miguan.ballvideo.service;

/**
 * Created by shixh on 2019/9/23.
 */
public interface WarnKeywordService {

  /**
   * 初始化敏感词
   * */
  void initWarnKeyword();

  /**
   * 初始化IOS敏感词
   * */
  void initWarnKeywordIos();

}
