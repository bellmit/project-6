package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.bigdata.entity.SysConfig;

import java.util.List;

/**
 * 系统参数Dao
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@DS("xy-db")
public interface SysConfigMapper extends BaseMapper {

    /**
     * 查询所有系统配置
     *
     * @return
     */
    @DS("xy-db")
    List<SysConfig> findAll();

    SysConfig getConfig(String code);

}
