package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.VideosCat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideosCatDao extends JpaRepository<VideosCat, Long> {
    List<VideosCat> findListByTypeOrderBySortAsc(Long type);

    @Query(value = "SELECT * FROM  videos_cat WHERE type = 1 AND status = ?1 ORDER BY sort ASC",nativeQuery = true)
    List<VideosCat> findVideosCatList(Long status);
}
