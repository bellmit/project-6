package com.miguan.recommend.service.mongo;

import com.miguan.recommend.bo.CatWeightDto;
import com.miguan.recommend.entity.mongo.UserRawTags;

import java.util.List;

public interface UserRawTagsService {

    /**
     * 根据UUID获取用户离线标签
     * @param uuid
     * @return
     */
    public List<UserRawTags> findByUUid(String uuid);

    /**
     * 根据UUID获取用户选择的标签对应的分类权重
     * @param uuid
     * @return
     */
    public List<UserRawTags> findChooseCatByUUid(String uuid);

    /**
     * 根据UUID获取用户的离线兴趣分类，并根据兴趣度进行倒序排序
     * @param uuid
     * @return
     */
    public List<Integer> findOfflineCatsByUUid(String uuid);

    /**
     * 根据UUID获取用户的离线兴趣分类，并获取用户最感兴趣的标签
     * @param uuid
     * @return
     */
    Integer findTopType(String uuid);

    /**
     * 获取用户分类权重，并做对数计算
     * @param uuid
     * @param tagId
     * @return
     */
    public List<CatWeightDto> findUserCatWeightsAndLog10(String uuid, Integer tagId);
}
