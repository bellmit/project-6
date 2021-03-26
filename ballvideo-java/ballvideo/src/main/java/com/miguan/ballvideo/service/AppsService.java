package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.vo.AppsVo;

public interface AppsService {

    /**
     * 上传APP列表
     * @param appsVo
     * @return
     */
    ResultMap uploadApps(AppsVo appsVo);

    /**
     * 上传APP列表保存到mongodb
     * @param appsVo
     */
    void saveToMongodb(AppsVo appsVo);

    /**
     * 创建mongodb索引
     */
    void createIndex();
}
