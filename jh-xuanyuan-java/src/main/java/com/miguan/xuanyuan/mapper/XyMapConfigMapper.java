package com.miguan.xuanyuan.mapper;

import com.github.pagehelper.Page;
import com.miguan.xuanyuan.entity.XyMapConfig;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.vo.XyMapConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 参数配置
 * @Date 2021/1/21
 **/
public interface XyMapConfigMapper extends BaseMapper<XyMapConfig> {
    Page<XyMapConfigVo> findPageList(Map params);
    List<XyMapConfig> findOpenConfig();

    int judgeExistCode(@Param("configKey") String configKey, @Param("id") Long id);
}
