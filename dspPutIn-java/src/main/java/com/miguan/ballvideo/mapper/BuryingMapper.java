package com.miguan.ballvideo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface BuryingMapper {

    /**
     * 动态添加数据
     * @param tableName 表
     * @param data 数据
     */
    @Insert("<script>insert into ${tn} <foreach collection=\"data.keys\" item=\"key\" open=\"(\" close=\")\" separator=\",\"> ${key} </foreach> " +
            "values <foreach collection=\"data.values\" item=\"value\" open=\"(\" close=\")\" separator=\",\"> #{value} </foreach></script>")
    void insertDynamic(@Param("tn") String tableName, @Param("data") Map<String, Object> data);
}
