package com.miguan.laidian.mapper;


import com.miguan.laidian.entity.TelBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TelBrandMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TelBrand record);

    int insertSelective(TelBrand record);

    TelBrand selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TelBrand record);

    int updateByPrimaryKey(TelBrand record);

    TelBrand selectTelBrandByTelKey(TelBrand telBrand);

    List<TelBrand> selectTelBrandByTelKeyList(@Param("telKeyList") List<String> telKey);
}