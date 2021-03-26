package com.miguan.reportview.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.miguan.reportview.entity.AdAdvertPosition;
import com.miguan.reportview.service.IAdAdvertPositionService;
import com.miguan.reportview.service.IAdAdvertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-08-07 
 *
 */
@Component
@Slf4j
public class ExportAdpositionTask {
    @Autowired
    private IAdAdvertService adAdvertService;
    @Autowired
    private IAdAdvertPositionService adAdvertPositionService;

    @Scheduled(cron = "${task.scheduled.cron.export-adposition}")
    public void exportAdposition() {
        adAdvertPositionService.remove(Wrappers.<AdAdvertPosition>emptyWrapper());
        List<AdAdvertPosition> list = adAdvertService.getAll();
        if(!isEmpty(list)){
            adAdvertPositionService.saveBatch(list);
        }
    }
}
