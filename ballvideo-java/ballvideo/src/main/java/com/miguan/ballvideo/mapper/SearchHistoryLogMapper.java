package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.vo.SearchHistoryLogVo;

/**
 * 搜索历史记录Mapper
 * @author laiyudan
 * @date 2020-10-21
 **/

public interface SearchHistoryLogMapper {

    /**
     * 查询搜索记录
     * @param logVo
     * @return
     */
    Integer findSearchInfo(SearchHistoryLogVo logVo);

    /**
     * 保存搜索记录
     * @param logVo
     * @return
     */
    int saveSearchInfo(SearchHistoryLogVo logVo);

}