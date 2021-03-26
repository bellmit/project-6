package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.entity.npush.PushDataIterArcticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PushDataIterArcticleMapper {

    @DS("npush-db")
    public List<PushDataIterArcticle> selectByActDayAndSortNum(@Param("projectType") Integer projectType, @Param("actDay") Integer actDay, @Param("sortNum") Integer sortNum);

    @DS("npush-db")
    public List<PushDataIterArcticle> selectByActDayAndAfterSortNum(@Param("projectType") Integer projectType, @Param("actDay") Integer actDay, @Param("sortNum") Integer sortNum);
}
