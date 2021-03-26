package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.ActUserDrawRecord;
import com.miguan.laidian.entity.ActUserDrawRecordExample;
import com.miguan.laidian.mapper.ActUserDrawRecordMapper;
import com.miguan.laidian.service.ActUserDrawRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Service
@Transactional
public class ActUserDrawRecordServiceImpl implements ActUserDrawRecordService {
    @Resource
    private ActUserDrawRecordMapper actUserDrawRecordMapper;
    /**
     * 获取用户碎片记录
     *
     * @param deviceId
     * @param activityId
     * @param activityConfigId
     * @return
     */
    @Override
    public List<ActUserDrawRecord> getUserDarwRecord(String deviceId, Long activityId, Long activityConfigId) {
        ActUserDrawRecordExample recordExample = new ActUserDrawRecordExample();
        ActUserDrawRecordExample.Criteria criteria = recordExample.createCriteria();
        criteria.andDeviceIdEqualTo(deviceId).andActivityIdEqualTo(activityId).andStateEqualTo(1).andIsExchangeEqualTo(0);
        if(activityConfigId != null){
            criteria.andActivityConfigIdEqualTo(activityConfigId);
        }
        return actUserDrawRecordMapper.selectByExample(recordExample);
    }

    /**
     * 保存记录
     *
     * @param activityId
     * @param activityConfigId
     * @param deviceId
     */
    @Override
    public ActUserDrawRecord saveRecord(Long activityId, Long activityConfigId, String deviceId) {
        ActUserDrawRecord record = new ActUserDrawRecord();
        record.setActivityId(activityId);
        record.setActivityConfigId(activityConfigId);
        record.setDeviceId(deviceId);
        record.setState(0);
        record.setCreatedAt(new Date());
        actUserDrawRecordMapper.insertSelective(record);
        return record;
    }

    /**
     * 更新记录状态
     *
     * @param deviceId
     * @param darwRecordId
     * @return
     */
    @Override
    public int updateRecordState(String deviceId, Long darwRecordId) {
        ActUserDrawRecordExample recordExample = new ActUserDrawRecordExample();
        recordExample.createCriteria().andDeviceIdEqualTo(deviceId).andIdEqualTo(darwRecordId);
        ActUserDrawRecord record = new ActUserDrawRecord();
        record.setState(1);
        return actUserDrawRecordMapper.updateByExampleSelective(record,recordExample);
    }

    /**
     * 更新用户碎片状态为已兑换
     *
     * @param deviceId
     * @param activityConfigId
     * @param debrisReachNum
     */
    @Override
    public int updateUserIsEchangeState(String deviceId, Long activityConfigId, Integer debrisReachNum) {
        return actUserDrawRecordMapper.updateUserIsEchangeState(deviceId,activityConfigId,debrisReachNum);
    }
}
