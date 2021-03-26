package com.miguan.advert.domain.service;
import com.miguan.advert.common.util.ResultMap;

public interface MigratingDataService {

    /**
     * 迁移广告配置信息至新表
     * @return
     */
    ResultMap migratingAdConfigData();

    /**
     * 迁移广告配置信息至新表
     * @return
     */
    ResultMap migratingAdConfigDataDefalut();

    /**
     * 删除实验的信息
     * @return
     * @param positionId
     * @param b
     */
    ResultMap moveAbConfigData(Integer positionId, boolean b);

    ResultMap rollback();
}
