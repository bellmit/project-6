package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;

/**
 * @Author shixh
 * @Date 2020/4/13
 **/
public interface TaskSearchService {

    ResultMap searchVivo(String taskId,String appPackage) throws Exception;

    ResultMap searchYouMeng(String taskId,String appPackage)throws Exception;

    ResultMap searchXiaoMi(String taskId,String appPackage)throws Exception;

}
