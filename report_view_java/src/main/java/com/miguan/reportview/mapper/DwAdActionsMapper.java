package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.dto.EarlyWarningDto;
import com.miguan.reportview.entity.DwAdActions;
import com.miguan.reportview.vo.AdBehaviorDataVo;
import com.miguan.reportview.vo.AdBehaviorSonDataVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频数据宽表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface DwAdActionsMapper extends BaseMapper<DwAdActions> {

    List<AdBehaviorDataVo> getData(Map<String, Object> params);

    List<AdBehaviorDataVo> getNewData(Map<String, Object> params);

    List<AdBehaviorSonDataVo> getSonData(Map<String, Object> params);

    List<EarlyWarningDto> findEarlyWarnList(Map<String, Object> params);
}
