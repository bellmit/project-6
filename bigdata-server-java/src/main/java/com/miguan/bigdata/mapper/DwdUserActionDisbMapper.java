package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

public interface DwdUserActionDisbMapper {

    /**
     * 查询最后一次访问日期
     *
     * @param packageName 包名
     * @param distinctId  设备ID
     * @return yyyyMMdd
     */
    @DS("ck-dw")
    public Integer selectLastVisitDate(@Param("packageName") String packageName, @Param("distinctId") String distinctId);
}
