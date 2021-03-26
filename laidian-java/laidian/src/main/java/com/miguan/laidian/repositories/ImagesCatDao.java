package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.ImagesCat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesCatDao extends JpaRepository<ImagesCat, Long> {
    List<ImagesCat> findAllByOrderBySortAsc();
}
