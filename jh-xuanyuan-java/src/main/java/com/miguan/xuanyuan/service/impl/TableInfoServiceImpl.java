package com.miguan.xuanyuan.service.impl;

import com.google.common.collect.Maps;
import com.miguan.xuanyuan.mapper.TableInfoMapper;
import com.miguan.xuanyuan.service.TableInfoService;
import com.miguan.xuanyuan.vo.common.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TableInfoServiceImpl implements TableInfoService {

    @Resource
    private TableInfoMapper tableInfoMapper;

    public List<TableInfo> findTableInfo(String tableName){
        return tableInfoMapper.findTableInfo(tableName);
    }

    @Override
    public String findTableColumnCommon(String tableName, String columnName) {
        Map params = Maps.newHashMap();
        params.put("tableName",tableName);
        params.put("columnName",columnName);
        return tableInfoMapper.findTableColumnCommon(params);
    }
}
