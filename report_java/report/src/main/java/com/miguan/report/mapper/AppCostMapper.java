package com.miguan.report.mapper;

import com.miguan.report.entity.AppCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhongli
 * @date 2020-06-20 
 *
 */
public interface AppCostMapper {

    int addAppCost(@Param("datas") List<AppCost> datas);
}
