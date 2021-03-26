package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ActUserPrizeExchangeRecord;
import com.miguan.laidian.entity.ActUserPrizeExchangeRecordExample;
import java.util.List;
import java.util.Map;

import com.miguan.laidian.vo.ActExchangeRecordVo;
import org.apache.ibatis.annotations.Param;

public interface ActUserPrizeExchangeRecordMapper {
    long countByExample(ActUserPrizeExchangeRecordExample example);

    int deleteByExample(ActUserPrizeExchangeRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActUserPrizeExchangeRecord record);

    int insertSelective(ActUserPrizeExchangeRecord record);

    List<ActUserPrizeExchangeRecord> selectByExample(ActUserPrizeExchangeRecordExample example);

    ActUserPrizeExchangeRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActUserPrizeExchangeRecord record, @Param("example") ActUserPrizeExchangeRecordExample example);

    int updateByExample(@Param("record") ActUserPrizeExchangeRecord record, @Param("example") ActUserPrizeExchangeRecordExample example);

    int updateByPrimaryKeySelective(ActUserPrizeExchangeRecord record);

    int updateByPrimaryKey(ActUserPrizeExchangeRecord record);

    /**
     * 查询兑奖记录
     *
     * @param params
     * @return
     */
    List<ActExchangeRecordVo> queryExchangeRecord(Map<String,Object> params);
}