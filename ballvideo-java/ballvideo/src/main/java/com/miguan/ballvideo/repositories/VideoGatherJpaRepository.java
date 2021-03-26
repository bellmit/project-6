package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.VideoGather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoGatherJpaRepository extends JpaRepository<VideoGather, Long> {

    List<VideoGather> findByStateAndRecommendStateOrderByBaseWeightDesc(int open, int recommend);
}
