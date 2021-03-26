package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserBuryingPointDay;
import com.miguan.ballvideo.entity.UserBuryingPointMonth;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserBuryingPointMonthJpaRepository extends JpaRepository<UserBuryingPointMonth, Long> {
    
}
