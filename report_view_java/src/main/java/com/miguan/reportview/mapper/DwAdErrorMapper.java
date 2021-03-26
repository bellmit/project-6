package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.DwAdErrors;
import com.miguan.reportview.vo.AdErrorDataVo;

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
public interface DwAdErrorMapper extends BaseMapper<DwAdErrors> {

    List<AdErrorDataVo> getData(Map<String, Object> params);
}
