package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.result.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PositionInfoMapper {

    /**
     * 查询广告位置列表信息
     * @return
     */
    List<AppAdPositionVo> getAppAdPositionName(Map<String, Object> param);

    /**
     * 查询广告配置列表信息
     * @return
     */
    List<PositionDetailInfoVo> getConfigInfoList(Map<String, Object> param);

    /**
     * 查询广告渲染等信息
     * @param param
     * @return
     */
    List<TypeInfoVo> getTypeData(Map<String, Object> param);

    /**
     *  查询代码位信息
     * @param param`
     * @return
     */
    List<PositionInfoVo> getPosition(Map<String, Object> param);

    /**
     *  查询代码位信息
     * @param param`
     * @return
     */
    List<ConfigInfoVo> getCodeConfig(Map<String, Object> param);

    /**
     * 删除配置代码位明细
     * @param param
     * @return
     */
    int deleteCodeConfig(Map<String, Object> param);

    /**
     * 新增配置代码位明细
     * @param configInfoVo
     * @return
     */
    int addCodeConfig(@Param("configInfoVoList") List<ConfigInfoVo> configInfoVo);

    /**
     * 修改配置代码位明细
     * @param param
     * @return
     */
    int updateCodeConfig(Map<String, Object> param);

    /**
     * 修改配置信息
     * @param param
     * @return
     */
    int updateConfig(Map<String, Object> param);

    /**
     * 根据code_id 获得对应的position_name
     * @param codeId
     * @return
     */
    String[] getPositionName(Integer codeId);

    AppAdPositionVo getPositionById(Integer positionId);
}
