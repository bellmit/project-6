package com.miguan.xuanyuan.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyOperationLog;
import com.miguan.xuanyuan.mapper.XyOperationLogMapper;
import com.miguan.xuanyuan.service.XyOperationLogService;
import com.miguan.xuanyuan.vo.XyOperationLogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 操作日志业务
 * @Date 2021/1/21
 **/
@Service
public class XyOperationLogServiceImpl implements XyOperationLogService {

    @Resource
    private XyOperationLogMapper mapper;

    @Override
    public PageInfo<XyOperationLogVo> pageList(String startDate, String endDate, Integer operationPlat, String pathName, Integer type, String keyword, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        params.put("operationPlat",operationPlat);
        params.put("pathName",pathName);
        params.put("type",type);
        params.put("keyword",keyword);
        PageHelper.startPage(pageNum, pageSize);
        Page<XyOperationLogVo> pageResult = mapper.findPageList(params);
        return new PageInfo(pageResult);
    }

    @Override
    public void save(XyOperationLog log) {
        mapper.insert(log);
    }
}
