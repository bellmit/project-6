package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.OperateLog;
import com.miguan.ballvideo.repositories.OperateLogJpaRepository;
import com.miguan.ballvideo.service.OperateLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogJpaRepository operateLogJpaRepository;

    @Override
    public void save(OperateLog operateLog) {
        operateLogJpaRepository.save(operateLog);
    }
}
