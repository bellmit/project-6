package com.miguan.bigdata.service;

import com.alibaba.fastjson.JSONObject;

public interface ManualService {

    public Long countPush(String appPackage, String catids);

    public void findAndSendToMq(String type, String businessId, JSONObject param);
}
