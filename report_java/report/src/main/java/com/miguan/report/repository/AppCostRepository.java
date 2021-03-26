package com.miguan.report.repository;

import com.miguan.report.entity.AppCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface AppCostRepository extends JpaRepository<AppCost, Long> {


    void deleteByDateAndAppNameAndAppTypeAndClientIdAndChannel(Date date, String appName, int appType, int clientId, String channel);

    AppCost findByDateAndAppNameAndAppTypeAndClientIdAndChannel(Date date, String appName, int appType, int clientId, String channel);

    long countByDateAndAppNameAndAppTypeAndClientIdAndChannel(Date date, String appName, int appType, int clientId, String channel);
}
