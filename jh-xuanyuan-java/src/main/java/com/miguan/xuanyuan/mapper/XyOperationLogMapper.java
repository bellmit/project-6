package com.miguan.xuanyuan.mapper;

import com.github.pagehelper.Page;
import com.miguan.xuanyuan.entity.XyOperationLog;
import com.miguan.xuanyuan.vo.XyOperationLogVo;

import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 操作日志
 * @Date 2021/1/21
 **/
public interface XyOperationLogMapper{
    int insert(XyOperationLog xyOperationLog);
    Page<XyOperationLogVo> findPageList(Map params);
}
