package com.miguan.recommend.service;

import com.alibaba.fastjson.JSONObject;

public interface PushService {

    public Long countPush(String appPackage, String catids);

    public void findAndSendToMq(String type, JSONObject param);
}
