package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.dto.request.SourceAppRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XySourceApp;
import com.miguan.xuanyuan.vo.AdCodeVo;
import com.miguan.xuanyuan.vo.RelateAppInfoVo;
import com.miguan.xuanyuan.vo.StrategyCodeVo;
import com.miguan.xuanyuan.vo.sdk.SourceAppInfoVo;

import java.util.List;

public interface XySourceAppService {

    int insert(SourceAppRequest sourceAppRequest);

    int update(SourceAppRequest sourceAppRequest) throws ServiceException;

    XySourceApp findById(Long id);

    XySourceApp getDataByAppIdAndPlatId(Long appId, Long platId);

    void putSourceApp(Long appId, Long platId, String sourceAppId) throws ServiceException;

    List<RelateAppInfoVo> relateAppInfo(Long appId);

    void saveRelateApp(List<RelateAppInfoVo> relationInfo) throws ValidateException;

    String findSourceAppId(Long appId, String platKey);

    String findSourceAppByPositionIdAndPlatKey(Long positionId, String platKey);

    List<SourceAppInfoVo> findAppInfo(Long appId);

    void createInnerApp(Long id);
}
