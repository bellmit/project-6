package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 市场审核开关表
 * @Author shixh
 **/
public interface MarketAuditDao extends JpaRepository<MarketAudit, Long> {

    @Cacheable(value = CacheConstant.GET_CATIDS_BY_CHANNELID_AND_APPVERSION, unless = "#result == null")
    @Query(value = "select *  from market_audit where state=1 and find_in_set(:channelId,channel_id) and " +
            " concat(lpad(SUBSTRING_INDEX(:version,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(:version,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(:version,'.',-1), 3, '0')) + 0 "+
            " BETWEEN " +
            " concat(lpad(SUBSTRING_INDEX(version1,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version1,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(version1,'.',-1), 3, '0')) + 0 "+
            " and " +
            " concat(lpad(SUBSTRING_INDEX(version2,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version2,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(version2,'.',-1), 3, '0')) + 0 ", nativeQuery = true)
    public MarketAudit getCatIdsByChannelIdAndAppVersion(@Param("channelId") String channelId, @Param("version") String version);

    @Cacheable(value = CacheConstant.GET_CATIDS_BY_CHANNELID_AND_APPVERSION_FromTeenager, unless = "#result == null")
    @Query(value = "select teenager_cat_ids from market_audit where teenager_state=1 and find_in_set(:channelId,channel_id) and " +
            " concat(lpad(SUBSTRING_INDEX(:version,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(:version,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(:version,'.',-1), 3, '0')) + 0 "+
            " BETWEEN " +
            " concat(lpad(SUBSTRING_INDEX(teenager_version1,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(teenager_version1,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(teenager_version1,'.',-1), 3, '0')) + 0 "+
            " and " +
            " concat(lpad(SUBSTRING_INDEX(teenager_version2,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(teenager_version2,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(teenager_version2,'.',-1), 3, '0')) + 0 ", nativeQuery = true)
    public String getCatIdsByChannelIdAndAppVersionFromTeenager(@Param("channelId") String channelId, @Param("version") String version);
}
