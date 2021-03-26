package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyPlatApp;
import com.miguan.xuanyuan.vo.AdCodeVo;
import com.miguan.xuanyuan.vo.StrategyCodeVo;
import com.miguan.xuanyuan.vo.StrategyVo;

import java.util.List;

public interface XyAdCodeService {

    /**
     * 添加广告源
     * @param adCodeRequest
     * @return 返回广告源id
     */
    public long insert(AdCodeRequest adCodeRequest) throws ServiceException;

    public int update(AdCodeRequest adCodeRequest) throws ServiceException;

    public AdCodeVo findById(Long id);

    public StrategyCodeVo addCode(AdCodeRequest adCodeRequest) throws ServiceException;

    public StrategyCodeVo editCode(AdCodeRequest adCodeRequest) throws ServiceException;

    XyAdCode getDataByPlatKeyAndCodeId(String sourcePlatKey, String sourceCodeId, Long id);

    Long getCodePutInStatus(String codeId);

    int updateCodeNotPutIn(Long positionId);

    void deleteByPositionId(Long positionId);

    void deleteByAppId(Long appId);

    String findSourceAppId(Long appId, String platKey);

    List<XyAdCode> findByAppIdAndPlatKey(Long appId, String platKey);
}
