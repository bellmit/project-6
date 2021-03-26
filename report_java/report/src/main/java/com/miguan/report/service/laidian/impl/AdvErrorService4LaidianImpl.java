package com.miguan.report.service.laidian.impl;

import com.miguan.report.common.dynamicquery.DynamicQuery4LaiDian;
import com.miguan.report.service.laidian.AdvErrorService4Laidian;
import com.miguan.report.vo.AdErrorVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

@Service
public class AdvErrorService4LaidianImpl implements AdvErrorService4Laidian {

    @Resource
    private DynamicQuery4LaiDian dynamicQuery4LaiDian;

    @Override
    public List<AdErrorVo> findAdError(String date, String adId) throws SQLSyntaxErrorException {
        String sql = "SELECT a.ad_error addError, COUNT(a.id) errorCount, (COUNT(a.id) / c.count) errorRate from ad_error_" + date + " a JOIN (SELECT COUNT(id) count from ad_error_" + date + " where ad_id = '" + adId + "' and DATE_FORMAT(creat_time,'%Y%m%d') = '" + date + "') c where a.ad_id = '" + adId + "' and DATE_FORMAT(a.creat_time,'%Y%m%d') = '" + date + "' GROUP BY a.ad_error";
        return dynamicQuery4LaiDian.nativeQueryList(AdErrorVo.class, sql);
    }
}
