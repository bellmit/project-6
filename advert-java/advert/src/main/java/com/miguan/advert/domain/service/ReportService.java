package com.miguan.advert.domain.service;

import com.miguan.advert.config.redis.util.CacheConstant;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import com.miguan.advert.domain.vo.interactive.AdCpmVo;
import com.miguan.advert.domain.vo.interactive.AdProfitVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface ReportService {

    @Cacheable(value = CacheConstant.COUNT_FORBIDDEN_VERSION, unless = "#result == null")
    int countChannelCost();

    /**
     * 查询当天穿山甲，广点通，快手的cpm数据是否已经全部导入完毕
     * @param date
     * @return
     */
    boolean isAllPlatFormReady(String date);


    /**
     * 查询代码位的cpm
     * @param date
     * @return
     */
    List<AdCpmVo> listAdCpmList(String date);


    /**
     * 获取第三方代码位最新历史收益
     *
     * @param adCode
     * @return
     */
    AdProfitVo getAdCodeLastProfit(String adCode, String date);

    /**
     * 获取第三方平台完整数据的最近时间
     *
     * @param platCount
     * @return
     */
    String getCompletePlatDataLastDate(int platCount);

}
