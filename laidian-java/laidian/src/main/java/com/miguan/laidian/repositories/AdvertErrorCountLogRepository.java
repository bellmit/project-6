package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.AdvertErrorCountLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertErrorCountLogRepository extends JpaRepository<AdvertErrorCountLog, Long> {
    AdvertErrorCountLog findFirstByAdIdAndCreatTimeAndDeviceIdAndAppPackageAndAppVersion(String adId, String creatTime, String deviceId, String appPackage, String appVersion);
}
