package com.miguan.xuanyuan.mapper;

import com.github.pagehelper.Page;
import com.miguan.xuanyuan.entity.XyOperationLog;
import com.miguan.xuanyuan.entity.XyOptionConfig;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.vo.XyOptionConfigVo;

import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 选项配置
 * @Date 2021/1/21
 **/
public interface XyOptionConfigMapper extends BaseMapper<XyOptionConfig> {
    Page<XyOptionConfigVo> findPageList(Map params);
    int judgeExistCode(String configCode);
}
