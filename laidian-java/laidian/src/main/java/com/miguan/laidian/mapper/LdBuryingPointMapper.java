package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.LdBuryingPoint;
import com.miguan.laidian.redis.util.CacheConstant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Map;

public interface LdBuryingPointMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LdBuryingPoint record);

    int ldBuryingPoint(LdBuryingPoint record);

    LdBuryingPoint selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LdBuryingPoint record);

    int updateByPrimaryKey(LdBuryingPoint record);

    @Cacheable(value = CacheConstant.LDBURYINGPOINT, unless = "#result == null")
    LdBuryingPoint selectByDeviceIdAndAppTypeOrderByCreateTimeAsc(@Param("deviceId") String deviceId, @Param("appType") String appType);

    LdBuryingPoint selectByDeviceIdAndAppTypeOrderByCreateTimeDESC(@Param("deviceId") String deviceId, @Param("appType") String appType);

    int updateByIdAndTimeDESC(LdBuryingPoint record);

    /**
     * 动态添加数据
     * @param tableName 表
     * @param data 数据
     */
    @Insert("<script>insert into ${tn} <foreach collection=\"data.keys\" item=\"key\" open=\"(\" close=\")\" separator=\",\"> ${key} </foreach> " +
            "values <foreach collection=\"data.values\" item=\"value\" open=\"(\" close=\")\" separator=\",\"> #{value} </foreach></script>")
    void insertDynamic(@Param("tn") String tableName, @Param("data") Map<String, Object> data);

    @Modifying
    @Select("<script>CREATE TABLE If Not Exists ${tn} LIKE ld_burying_point_every</script>")
    void createTableDynamic(@Param("tn") String tableNameDateStr);
}