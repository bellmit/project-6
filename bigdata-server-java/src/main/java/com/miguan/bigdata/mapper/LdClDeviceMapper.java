package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.entity.xy.ClDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LdClDeviceMapper {

    @DS("laidian-db")
    public List<ClDevice> selectByDeviceId(@Param("deviceId") String deviceId);
    @DS("laidian-db")
    public List<ClDevice> selectByDistinctId(@Param("distinctIds") List<String> distinctIds);
    @DS("laidian-db")
    public List<ClDevice> selectByDistinctIdAndPackageName(@Param("packageName") String packageName, @Param("distinctId") String distinctId);
}
