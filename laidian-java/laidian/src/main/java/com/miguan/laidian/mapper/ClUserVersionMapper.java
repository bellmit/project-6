package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.ClUserVersion;
import org.apache.ibatis.annotations.Param;

public interface ClUserVersionMapper {

    int findUserVersionByVersionId(@Param("version") String version,@Param("appType")String appType);


    int insertSelective(ClUserVersion record);
}
