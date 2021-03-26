package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ManulPushDistinctMapper {

    @DS("npush-db")
    public Long deleteByDt(@Param("dt") Integer dt);

    @DS("npush-db")
    public Long countByParams(@Param("businessId") String businessId, @Param("packageName") String packageName,
                              @Param("channels") List<String> channels);

    @DS("npush-db")
    public List<String> selectByParams(@Param("businessId") String businessId, @Param("packageName") String packageName,
                                       @Param("channels") List<String> channels, @Param("skip") Integer skip,
                                       @Param("limit") Integer limit);
}
