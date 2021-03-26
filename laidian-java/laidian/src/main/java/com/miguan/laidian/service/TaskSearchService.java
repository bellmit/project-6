package com.miguan.laidian.service;


import com.miguan.laidian.common.util.ResultMap;

/**
 * @Author shixh
 * @Date 2020/4/13
 **/
public interface TaskSearchService {

    ResultMap searchVivo(String taskId, String appType) throws Exception;

    ResultMap searchYouMeng(String taskId, String appType) throws Exception;

    ResultMap searchXiaoMi(String taskId, String appType) throws Exception;

}
