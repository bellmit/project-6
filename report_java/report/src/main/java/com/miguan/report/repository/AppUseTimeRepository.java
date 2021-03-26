package com.miguan.report.repository;

import com.miguan.report.entity.report.AppUseTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUseTimeRepository extends JpaRepository<AppUseTime, Long> {

    void deleteAllByUseDay(String useDay);

    int countByUseDay(String useDay);
}
