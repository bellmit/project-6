package com.miguan.advert.domain.serviceimpl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.advert.domain.mapper.AdOperationLogMapper;
import com.miguan.advert.domain.service.AdOperationLogService;
import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdOperationLogQuery;
import com.miguan.advert.domain.vo.result.AdOperationLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AdOperationLogServiceImpl implements AdOperationLogService {

    @Resource
    private AdOperationLogMapper operationMapper;

    @Override
    public PageVo findPage(Integer page, Integer page_size, AdOperationLogQuery adOperationLogQuery) {
        PageHelper.startPage(page,page_size);
        Page<AdOperationLogVo> logList = operationMapper.findBySelective(adOperationLogQuery);
        if(logList == null){
            return null;
        }
        for (AdOperationLogVo operationLogVo:logList.getResult()) {
            operationLogVo.setChange_content(JSON.parseArray(operationLogVo.getC_content()).toArray());
        }
        return new PageVo(logList);
    }
}
