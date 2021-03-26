package com.miguan.laidian.service;

import com.miguan.laidian.entity.LdInterestTag;

import java.util.List;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 11:08
 * @Description:
 */
public interface LdInterestTagService {

    /**
     * 获取用户标签
     * @param userId
     * @return
     */
    List<LdInterestTag> getUserTags(String userId);

    /**
     * 保存用户tag，对个用逗号隔开
     * @param userId
     * @param tagIds
     * @return
     */
    Integer saveTag(String userId, String tagIds, String deviceId);

    /**
     * 获取全部标签
     * @return
     */
    List<LdInterestTag> getAllList();
}
