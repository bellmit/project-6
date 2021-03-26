package com.miguan.report.service.video;

import com.miguan.report.vo.AdErrorVo;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

public interface AdErrorService4Video {

    public List<AdErrorVo> findAdError(String date, String adId) throws SQLSyntaxErrorException;
}
