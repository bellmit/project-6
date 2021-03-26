package com.miguan.recommend.service.ck.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.recommend.mapper.DwVideoActionMapper;
import com.miguan.recommend.service.ck.DwVideoActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@DS("clickhouse")
@Service
public class DwVideoActionServiceImpl implements DwVideoActionService {

    @Resource
    private DwVideoActionMapper dwVideoActionMapper;

    @Override
    public List<Map<String, Object>> findSimilarCatid(String date, String catid) {
        return dwVideoActionMapper.findSimilarCatid(date, catid);
    }
}
