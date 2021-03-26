package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.dto.request.SourceAppRequest;
import com.miguan.xuanyuan.entity.XyPlat;
import com.miguan.xuanyuan.entity.XySourceApp;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.vo.RelateAppInfoVo;
import com.miguan.xuanyuan.vo.sdk.SourceAppInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XySourceAppMapper extends BaseMapper<XySourceApp> {

    int insert(SourceAppRequest sourceAppRequest);

    int update(SourceAppRequest sourceAppRequest);

    XySourceApp findById(Long id);

    XySourceApp getDataByAppIdAndPlatId(Long appId, Long platId);

    List<RelateAppInfoVo> findRelateApp(@Param("appId") Long appId);

    void saveRelateApp(@Param("relationInfoList")List<RelateAppInfoVo> relationInfo);

    String findSourceAppId(@Param("appId") Long appId,@Param("platKey") String platKey);

    String findSourceAppByPositionIdAndPlatKey(@Param("positionId") Long positionId,@Param("platKey") String platKey);

    int judgeExistSourceApp(SourceAppRequest sourceAppRequest);

    List<SourceAppInfoVo> findAppInfo(Long appId);

    List<XyPlat> findInnerApp(Long appId);
}
