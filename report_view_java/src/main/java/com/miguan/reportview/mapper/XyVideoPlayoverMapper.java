package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.XyVideoPlayover;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XyVideoPlayoverMapper extends BaseMapper<XyVideoPlayover> {

    void saveBatch(@Param("data") List<XyVideoPlayover> data);
}
