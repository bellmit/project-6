package com.miguan.report.service.laidian;

import com.miguan.report.vo.AdErrorVo;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

public interface AdvErrorService4Laidian {

    public List<AdErrorVo> findAdError(String date, String adId) throws SQLSyntaxErrorException;
}
