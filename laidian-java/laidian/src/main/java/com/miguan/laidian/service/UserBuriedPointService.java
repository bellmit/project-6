package com.miguan.laidian.service;

import com.miguan.laidian.entity.LdBuryingPoint;
import com.miguan.laidian.entity.LdBuryingPointAdditional;
import com.miguan.laidian.entity.LdBuryingPointEvery;


public interface UserBuriedPointService {

    void userBuriedPointSecondEdition(LdBuryingPoint ldBuryingPoint);

    void ldBuryingPointAdditionalSecondEdition(LdBuryingPointAdditional ldBuryingPointAdditional) throws Exception;

    void userBuriedPointEvery(LdBuryingPointEvery ldBuryingPointEvery);

    boolean getUserState(String deviceId,String appType);
}
