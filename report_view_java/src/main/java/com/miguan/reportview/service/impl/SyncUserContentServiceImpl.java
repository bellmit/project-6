package com.miguan.reportview.service.impl;

import com.miguan.reportview.mapper.SyncUserContentMapper;
import com.miguan.reportview.service.SyncUserContentService;
import com.miguan.reportview.vo.SyncUserContentDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 同步用户内容运营数据和渠道数据表service
 * @Author zhangbinglin
 * @Date 2020/8/19 18:28
 **/
@Service
@Slf4j
public class SyncUserContentServiceImpl implements SyncUserContentService {

    @Resource
    private SyncUserContentMapper syncUserContentMapper;

    /**
     * 同步用户内容运营数据
     *
     * @param startDate 开始时间,如：2020-08-01
     * @param endDate   结束时间,如：2020-08-19
     */
    public void syncUserContent(String startDate, String endDate) {
        syncUserContentMapper.batchSaveActionData(startDate, endDate);
        syncUserContentMapper.deleteActionData(startDate, endDate);
        syncUserContentMapper.updateActionData(startDate, endDate);
    }

    /**
     * 同步来电用户事件数据
     * @param date 时间,如：2020-08-01
     */
    public void syncLdUserContent(String date) {
        syncUserContentMapper.batchSaveLdActionData(date);
        syncUserContentMapper.deleteLdActionData(date);
        syncUserContentMapper.updateLdActionData(date);
    }
}
