package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.BsyLog;
import com.miguan.ballvideo.repositories.BsyLogRepository;
import com.miguan.ballvideo.service.BsyLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BsyLogServiceImpl implements BsyLogService {

    @Resource
    BsyLogRepository bsyLogRepository;

    @Override
    public void saveBsyLog(BsyLog bsyLog) {
        bsyLogRepository.save(bsyLog);
    }

}
