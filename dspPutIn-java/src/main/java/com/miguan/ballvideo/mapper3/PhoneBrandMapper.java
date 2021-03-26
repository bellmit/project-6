package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.response.PhoneBrandRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 区域表
 * @Date 2020/11/20 16:44
 **/
public interface PhoneBrandMapper {
    List<PhoneBrandRes> findAll();

    List<PhoneBrandRes> findByBrand(@Param("brands") List<String> brands);
}
