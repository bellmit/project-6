package com.miguan.recommend.service;

import java.util.Map;

public interface ABTestService {

    /**
     * 获取实验信息
     *
     * @param expCode      实验标识
     * @param variableName 变量名
     * @return
     */
    public Map<String, String> getABTestGroupInfoByExpKey(String expCode, String variableName);
}
