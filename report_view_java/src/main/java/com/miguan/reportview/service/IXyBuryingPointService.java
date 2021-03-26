package com.miguan.reportview.service;

import java.time.LocalDateTime;

public interface IXyBuryingPointService {

    public void copyToClickHouseFromMongo(LocalDateTime localDateTime);
}
