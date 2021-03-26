package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserBuryingPoint;
import com.miguan.ballvideo.entity.UserBuryingPointDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserBuryingPointDayJpaRepository extends JpaRepository<UserBuryingPointDay, Long> {
    
}
