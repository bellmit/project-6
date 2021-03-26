package com.miguan.recommend.service.mongo;

import com.miguan.recommend.entity.mongo.UserOfflineLabel;

import java.util.List;

public interface UserOfflineLabelService {

    /**
     * 根据UUID查询用户离线分类兴趣度
     * @param uuid
     * @return
     */
    public List<UserOfflineLabel> findByUUid(String uuid);
}
