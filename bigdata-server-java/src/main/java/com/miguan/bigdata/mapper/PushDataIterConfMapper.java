package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.entity.npush.PushDataIterConf;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PushDataIterConfMapper {

    @DS("npush-db")
    public List<PushDataIterConf> selectByServenDay(@Param("dayOfWeek") int dayOfWeek);
}
