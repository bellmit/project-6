package com.miguan.advert.domain.service;

import com.miguan.advert.config.redis.util.CacheConstant;
import com.miguan.advert.domain.vo.interactive.AdCpmVo;
import com.miguan.advert.domain.vo.interactive.AdProfitVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Date;

public interface DspReportService {

    AdProfitVo getAdCodeLastProfit(String adCode, String date);

    /**
     * 查询98代码位的cpm
     * @param date
     * @return
     */
    List<AdCpmVo> listAd98CpmList(String date);
}
