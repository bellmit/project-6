package com.miguan.recommend.service.xy;

import java.util.List;

public interface NewUserSelectionService {

    /**
     * 根据分类ID获取首刷视频列表
     * @param catId
     * @return
     */
    public List<String> getVideoByCatId(Integer catId, List<Integer> excludeCatList);

    /**
     * 根据分类ID获取首刷视频列表
     * @return
     */
    public List<String> getDefaultVideo(List<Integer> excludeCatList);

    /**
     * 刷新首刷视频缓存
     * @param catIds
     */
    public void updateVideoByCatId(String catIds);

    /**
     * 移除屏蔽掉的视频
     * @param videoIds
     */
    public void removeTheVideoIsScreened(List<String> videoIds, List<Integer> excludeCatList);
}
