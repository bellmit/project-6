package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.HotWord;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotWordJpaRepository extends JpaRepository<HotWord, Long> {

    HotWord findTopByContent(String content);

    @Cacheable(value = CacheConstant.FIND_HOT_WORD_INFO, unless = "#result == null")
    @Query(value = "select * from hot_word where state=1 order by base_weight desc,updated_at desc limit 10", nativeQuery = true)
    List<HotWord> findHotWordInfo();

    @Cacheable(value = CacheConstant.FIND_HOT_WORD_INFO1, unless = "#result == null")
    @Query(value = "select * from hot_word where state=1 and id not in(?1) order by base_weight desc,updated_at desc limit 10", nativeQuery = true)
    List<HotWord> findHotWordList(List<Integer> ids);

    @Cacheable(value = CacheConstant.FIND_HOT_WORD_INFO4, unless = "#result == null")
    @Query(value = "select * from hot_word where state=1 and id in(?1)", nativeQuery = true)
    List<HotWord> findHotWordListNew(List<String> queryIds);

    @Cacheable(value = CacheConstant.FIND_HOT_WORD_INFO2, unless = "#result == null")
    @Query(value = "select * from hot_word where state=1 and id = ?1 limit 1", nativeQuery = true)
    HotWord findHotWordById(Long id);

    @Cacheable(value = CacheConstant.FIND_HOT_WORD_INFO3, unless = "#result == null")
    @Query(value = "select * from hot_word where admin_state=1 and content = ?1 limit 1", nativeQuery = true)
    HotWord findHotWordByContent(String content);
}
