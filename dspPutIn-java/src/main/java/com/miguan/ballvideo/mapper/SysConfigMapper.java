package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.SysConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 系统参数Dao
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
*/
public interface SysConfigMapper {

    /**
     * 查询所有系统配置
     * @return
     */
    List<SysConfigVo> findAll();

    SysConfigVo selectByCode(Map<String, Object> params);
}
