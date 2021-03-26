package com.miguan.report.repository;

import com.miguan.report.entity.report.ShenceData;
import com.miguan.report.entity.report.UmengData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface ShenceDataRepository extends JpaRepository<ShenceData, Long> {
    @Modifying
    @Transactional
    @Query(value = "delete from shence_data where date >= ?1 and date<= ?2 and app_type = ?3", nativeQuery = true)
    void deleteByDate(String startDate, String endDate, Integer appType);
}
