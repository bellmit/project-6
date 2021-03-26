package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.entity.AutoPushConfig;

import java.util.Map;


/**
 * 自动推送配置Mapper
 * @author cxy
 * @date 2020-03-31
 **/

public interface PushVideoMapper {

    AutoPushConfig queryById(Long id);
    int updateSenNumByVideoId(Map<String,Object> param);

}