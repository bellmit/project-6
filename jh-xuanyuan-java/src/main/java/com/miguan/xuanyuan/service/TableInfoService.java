package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.vo.common.TableInfo;

import java.util.List;

public interface TableInfoService {

    List<TableInfo> findTableInfo(String tableName);

    String findTableColumnCommon(String tableName,String columnName);

}
