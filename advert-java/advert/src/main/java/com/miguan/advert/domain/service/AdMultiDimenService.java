package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.interactive.AdMultiDimenVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;
import java.util.List;

public interface AdMultiDimenService {

    /**
     * 获取代码位统计数据
     * @param adId
     * @param packageName
     * @param channelId
     * @param userType
     * @param city
     * @param date
     * @return
     */
    AdMultiDimenVo getAdReport(String adId, String packageName, String channelId, int userType, String city, String date);

}
