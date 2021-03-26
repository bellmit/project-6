package com.miguan.laidian.service;

public interface AdErrorService {

    /**
     * 广告错误日志保存
     *
     * @param jsonMsg json格式
     */
    void addError(String jsonMsg);

    void advWrongDatas(String dateStr);
}
