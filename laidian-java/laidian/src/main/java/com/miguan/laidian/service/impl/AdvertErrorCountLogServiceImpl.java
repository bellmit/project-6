package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.entity.AdvertErrorCountLog;
import com.miguan.laidian.repositories.AdvertErrorCountLogRepository;
import com.miguan.laidian.service.AdvertErrorCountLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AdvertErrorCountLogServiceImpl implements AdvertErrorCountLogService {

    @Resource
    private AdvertErrorCountLogRepository advertErrorCountLogRepository;

    @Override
    public void save(List<AdvertErrorCountLog> datas) {
        for (AdvertErrorCountLog errorCountLog : datas) {
            AdvertErrorCountLog advertErrorCountLog = advertErrorCountLogRepository
                    .findFirstByAdIdAndCreatTimeAndDeviceIdAndAppPackageAndAppVersion(
                            errorCountLog.getAdId(),
                            errorCountLog.getCreatTime(),
                            errorCountLog.getDeviceId(),
                            errorCountLog.getAppPackage(),
                            errorCountLog.getAppVersion());
            try {
                if (advertErrorCountLog == null) {
                    advertErrorCountLogRepository.save(errorCountLog);
                } else {
                    advertErrorCountLog.setRenderFailed(errorCountLog.getRenderFailed() + advertErrorCountLog.getRenderFailed());
                    advertErrorCountLog.setRenderSuccess(errorCountLog.getRenderSuccess() + advertErrorCountLog.getRenderSuccess());
                    advertErrorCountLog.setRequestFailed(errorCountLog.getRequestFailed() + advertErrorCountLog.getRequestFailed());
                    advertErrorCountLog.setRequestSuccess(errorCountLog.getRequestSuccess() + advertErrorCountLog.getRequestSuccess());
                    advertErrorCountLog.setShowFailed(errorCountLog.getShowFailed() + advertErrorCountLog.getShowFailed());
                    advertErrorCountLog.setShowSuccess(errorCountLog.getShowSuccess() + advertErrorCountLog.getShowSuccess());
                    advertErrorCountLog.setTotalNum(errorCountLog.getTotalNum() + advertErrorCountLog.getTotalNum());
                    advertErrorCountLog.setAppTime(errorCountLog.getAppTime());
                    advertErrorCountLogRepository.save(advertErrorCountLog);
                }
            } catch (Exception e) {
                log.error("AdvertErrorCountLogSave_error："+ JSON.toJSONString(errorCountLog));
            }
        }
    }
}
