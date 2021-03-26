package com.miguan.report.repository;

import com.miguan.report.entity.BannerDataTotalName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface BannerDataTotalNameRepository extends JpaRepository<BannerDataTotalName, Long> {

    int deleteByDateAndPlatForm(Date date, int platForm);
}
