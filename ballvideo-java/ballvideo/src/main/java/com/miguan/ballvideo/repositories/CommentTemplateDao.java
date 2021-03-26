package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.CommentTemplate;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentTemplateDao extends JpaRepository<CommentTemplate, Long> {

    /**
     * 查询所有评论
     * @param state
     * @return
     */
    @Cacheable(value = CacheConstant.FIND_ALL_BY_STATE, unless = "#result == null || #result.size()==0")
    List<CommentTemplate> findAllByState(Long state);
}
