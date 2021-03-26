package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DwdUserInfoMapper {

    @DS("ck-dw")
    public Integer countByPackageNameAndFirstVisitDate(@Param("packageName") String packageName, @Param("active_date") String active_date,
                                                       @Param("channels") List<String> channels);

    @DS("ck-dw")
    public List<String> selectByPackageNameAndFirstVisitDate(@Param("packageName") String packageName, @Param("active_date") String active_date,
                                                               @Param("channels") List<String> channels,
                                                               @Param("skip") Integer skip, @Param("size") Integer size);

    @DS("ck-dw")
    public List<Map<String, Object>> selectMapByPackageNameAndFirstVisitDate(@Param("packageName") String packageName, @Param("active_date") String active_date,
                                                                             @Param("channels") List<String> channels,
                                                                             @Param("skip") Integer skip, @Param("size") Integer size);
}
