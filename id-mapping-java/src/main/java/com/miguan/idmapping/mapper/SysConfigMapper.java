package com.miguan.idmapping.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.idmapping.vo.SysConfigVo;

import java.util.List;

/**
 * 系统参数Dao
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
*/
public interface SysConfigMapper extends BaseMapper<SysConfigVo> {

    /**
     * 查询所有系统配置
     * @return
     */
    List<SysConfigVo> findAll();

}
