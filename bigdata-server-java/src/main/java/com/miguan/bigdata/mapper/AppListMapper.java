package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.entity.mongo.AppsInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户设备应用安装列表mapper
 */
@DS("xy-db")
public interface AppListMapper {

    /**
     * 根据包名和deviceId查询对应的distinct_id
     * @param list
     * @return
     */
    List<AppsInfo> findDistinctIdByDeviceId(@Param("list") List<AppsInfo> list);
}
