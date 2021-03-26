package com.miguan.ballvideo.service;


public interface AdErrorService {
    /**
     * 批量保存错误日志，
     * @param jsonMsg
     */
    void addError(String jsonMsg);
}
