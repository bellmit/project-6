package com.miguan.recommend.service.xy;

import java.util.List;

/**
 * 视频分类表Service
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface VideosCatService {

    /**
     * 获取所有视频分类
     *
     * @param type  类型 10首页视频 20 小视频
     * @return
     */
    List<String> getAllCatIds(String type);

    /**
     * 根据state，type查询分类名称
     *
     * @param state 1-开启，2-停用
     * @param type  类型 10首页视频 20 小视频
     * @return
     */
    List<String> getCatIdsByStateAndType(Integer state, String type);
}