package com.miguan.ballvideo.service;

/**
 * @Author shixh
 * @Date 2020/3/23
 **/
public interface SysService {

    void delRedis(String phPkey);

    void updateAdConfigCache();

    void updateAdLadderCache();
}
