package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.response.DistrictRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 区域表
 **/
public interface DistrictMapper {
    List<DistrictRes> getList(@Param("type")  int type,@Param("pid")  Long pid);

    List<DistrictRes> findByDistinctName(@Param("names") List<String> names, @Param("type") Integer type);
}
