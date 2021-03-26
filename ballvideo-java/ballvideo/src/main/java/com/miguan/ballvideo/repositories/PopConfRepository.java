package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.AbTestConfig;
import com.miguan.ballvideo.entity.PopConf;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PopConfRepository extends JpaRepository<PopConf, Long> {

    PopConf findFirstByPopPositionAndState(int popPosition, int state);
}
