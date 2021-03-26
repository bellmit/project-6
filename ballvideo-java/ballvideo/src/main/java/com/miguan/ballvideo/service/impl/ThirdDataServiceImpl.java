package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.AdPositionConfig;
import com.miguan.ballvideo.service.ThirdDataService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ThirdDataServiceImpl implements ThirdDataService{

    @Resource
    private HikariDataSource thirdDataSource;

    /**
     * 获取切片广告切片配置信息
     * @return
     */
    @Override
    public List<AdPositionConfig> getAdPositionConfigList(){
        String nativeSql = "select ad_position_id,csj_rate,gdt_rate,jbk_rate,keyword_mobileType,app_code from ad_position_config WHERE state = 1";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(thirdDataSource);
        List<AdPositionConfig> list =jdbcTemplate.query(nativeSql, (resultSet, i) -> {
            AdPositionConfig adPositionConfig = new AdPositionConfig();
            adPositionConfig.setAdPositionId(resultSet.getLong("ad_position_id"));
            adPositionConfig.setCsjRate(resultSet.getDouble("csj_rate"));
            adPositionConfig.setGdtRate(resultSet.getDouble("gdt_rate"));
            adPositionConfig.setJsbRate(resultSet.getDouble("jbk_rate"));
            adPositionConfig.setKeywordMobileType(resultSet.getString("keyword_mobileType"));
            adPositionConfig.setAppCode(resultSet.getString("app_code"));
            return adPositionConfig;
        });
        return list;
    }
}
