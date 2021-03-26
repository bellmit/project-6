package com.miguan.report.repository;

import com.miguan.report.entity.BannerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface BannerDataRepository extends JpaRepository<BannerData, Long> {
    @Modifying
    @Transactional
    @Query(value = "delete from banner_data where date = ?1 and plat_form = ?2", nativeQuery = true)
    void deleteByDateAndPlatForm(String date, Integer platForm);
}
