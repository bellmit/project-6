package com.miguan.idmapping.mapper;

import com.miguan.idmapping.entity.ClDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户表(cl_device)数据Mapper
 *
 * @author zhongli
 * @since 2020-09-01 11:40:54
 * @description 
*/
@Mapper
public interface ClDeviceMapper extends BaseMapper<ClDevice> {

    @Transactional
    @Update("update cl_device set distinct_id = #{distinctId} where device_id = #{deviceId} and app_package =#{appPackage}")
    void updateDistinctId(@Param("distinctId") String distinctId, @Param("deviceId") String deviceId, @Param("appPackage") String appPackage);
}
