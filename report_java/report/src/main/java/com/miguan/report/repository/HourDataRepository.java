package com.miguan.report.repository;

import com.miguan.report.entity.report.HourData;
import com.miguan.report.entity.report.UmengData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface HourDataRepository extends JpaRepository<HourData, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from hour_data where date = ?1", nativeQuery = true)
    void deleteHourDataByDate(String date);

    @Modifying
    @Transactional
    @Query(value = "update hour_data set click_rate = (case when show_number = 0 then 0 else truncate(click_number/show_number,4) end) * 100 where date = ?1", nativeQuery = true)
    int updateClickRate(String date);
}
