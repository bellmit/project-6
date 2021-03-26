package com.miguan.report.repository;

import com.miguan.report.entity.report.BannerDataExt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface BannerDataExtRepository extends JpaRepository<BannerDataExt, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from banner_data_ext where date = ?1", nativeQuery = true)
    void deleteByDate(String date);

    @Modifying
    @Transactional
    @Query(value = "update banner_data_ext set err_rate = (case when req_num = 0 then 0 else truncate(err_num/req_num,4) end) * 100 where date = ?1", nativeQuery = true)
    int updateErrRate(String date);
}
