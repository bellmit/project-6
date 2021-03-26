package com.miguan.advert.domain.service;

import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.vo.request.ConfigAddVo;
import com.miguan.advert.domain.vo.result.AppAdPositionVo;
import com.miguan.advert.domain.vo.result.CodeListVo;
import com.miguan.advert.domain.vo.result.PositionInfoVo;
import com.miguan.advert.domain.vo.result.PositionListVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PositionInfoService {

    PositionListVo getData(HttpServletRequest request, Integer type, String s_data, Integer page, Integer page_size);

    List<AppAdPositionVo> getAppAdPositionName(String app_package, Integer mobile_type);

    List<PositionInfoVo> getPosition(Integer position_id);

    List<CodeListVo> getCode(Integer position_id);

    ResultMap saveConfigInfo(String insertStr, String deleteStr, ConfigAddVo rateData);
}
