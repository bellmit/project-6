package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.entity.ActUserPrizeExchangeRecord;
import com.miguan.laidian.entity.ActUserPrizeExchangeRecordExample;
import com.miguan.laidian.mapper.ActUserPrizeExchangeRecordMapper;
import com.miguan.laidian.service.ActUserPrizeExchangeRecordService;
import com.miguan.laidian.vo.ActExchangeRecordVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Service
@Transactional
public class ActUserPrizeExchangeRecordServiceImpl implements ActUserPrizeExchangeRecordService {
    @Resource
    private ActUserPrizeExchangeRecordMapper actUserPrizeExchangeRecordMapper;

    /**
     * 获取设备兑奖记录
     *
     * @param deviceId
     * @param activityId
     * @return
     */
    @Override
    public List<ActUserPrizeExchangeRecord> getUserExchangeRecord(String deviceId, Long activityId) {
        ActUserPrizeExchangeRecordExample recordExample = new ActUserPrizeExchangeRecordExample();
        recordExample.createCriteria().andDeviceIdEqualTo(deviceId).andActivityIdEqualTo(activityId);
        return actUserPrizeExchangeRecordMapper.selectByExample(recordExample);
    }

    /**
     * 添加兑换记录
     *
     * @param activityId
     * @param activityConfigId
     * @param contastInfo
     * @param commomParams
     * @return
     */
    @Override
    public int addPrize(Long activityId, Long activityConfigId, String contastInfo,CommonParamsVo commomParams,String rcvrName, String rcvrAddr, String rcvrPhone) {
        ActUserPrizeExchangeRecord exchangeRecord = new ActUserPrizeExchangeRecord();
        exchangeRecord.setActivityId(activityId);
        exchangeRecord.setActivityConfigId(activityConfigId);
        exchangeRecord.setContastInfo(contastInfo);
        Date date = new Date();
        exchangeRecord.setCreatedAt(date);
        exchangeRecord.setUpdatedAt(date);
        exchangeRecord.setState(0);
        exchangeRecord.setDeviceId(commomParams.getDeviceId());
        String userId = commomParams.getUserId();
        if (!"0".equals(userId) && userId != null){
            exchangeRecord.setUserId(Long.valueOf(userId));
        }
        exchangeRecord.setRcvrName(rcvrName);//收货人姓名
        exchangeRecord.setRcvrAddr(rcvrAddr);//收货人地址
        exchangeRecord.setRcvrPhone(rcvrPhone);//收货人手机号
        return actUserPrizeExchangeRecordMapper.insertSelective(exchangeRecord);
    }

    @Override
    public List<ActExchangeRecordVo> queryExchangeRecord(Map<String,Object> params){

        return actUserPrizeExchangeRecordMapper.queryExchangeRecord(params);
    }
}
