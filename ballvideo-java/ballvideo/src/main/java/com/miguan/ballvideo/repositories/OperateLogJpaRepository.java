package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.OperateLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
public interface OperateLogJpaRepository extends JpaRepository<OperateLog, Long> {

}
