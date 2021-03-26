package com.miguan.advert.domain.service;


import com.miguan.advert.domain.vo.TableInfo;

import java.util.List;

public interface TableInfoService {

    List<TableInfo> findTableInfo(String tableName);

    String findTableColumnCommon(String tableName,String columnName);

}
