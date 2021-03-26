package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyOperationLog;
import com.miguan.xuanyuan.vo.XyOperationLogVo;

/**
 * @Author kangkunhuang
 * @Description 操作日志
 * @Date 2021/1/21
 **/
public interface XyOperationLogService {
    PageInfo<XyOperationLogVo> pageList(String startDate, String endDate, Integer operationPlat, String pathName, Integer type, String keyword, Integer pageNum, Integer pageSize);

    void save(XyOperationLog log);
}
