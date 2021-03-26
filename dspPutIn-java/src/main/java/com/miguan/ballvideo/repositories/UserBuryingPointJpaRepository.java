package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserBuryingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserBuryingPointJpaRepository extends JpaRepository<UserBuryingPoint, Long> {

}
