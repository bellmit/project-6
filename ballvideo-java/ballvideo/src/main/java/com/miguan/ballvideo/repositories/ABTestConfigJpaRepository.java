package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.AbTestConfig;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ABTestConfigJpaRepository extends JpaRepository<AbTestConfig, Long> {

    /**
     * 根据渠道ID查询AB测试配置信息
     * @param channelId
     * @return
     */
    @Cacheable(value = CacheConstant.FIND_ABTESTCONFIG, unless = "#result == null || #result.size()==0")
    @Query(value = "select * from ab_test_config a where a.state = 1 and now() BETWEEN a.gray_begin_time and a.gray_end_time " +
            "and (a.channel_id = ?1 or a.channel_id = '0') ",nativeQuery = true)
    List<AbTestConfig> findAbTestConfig(String channelId);

    /**
     * AB测试配置B面用户加1
     * @param id
     * @return
     */
    @Modifying
    @Query(value = "update ab_test_config ab set ab.b_user_num = ab.b_user_num + 1 where ab.id = ?1",nativeQuery = true)
    int updateAbUserConfig(Long id);
}
