package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.entity.XyRender;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 渲染方式
 * @Date 2021/1/21
 **/
public interface XyRenderMapper extends BaseMapper<XyRender> {
    int judgeExistKey(@Param("rKey") String rKey, @Param("id") Long id);
    List<XyRender> findList(@Param("platKey") String platKey,@Param("adType")  String adType);
}
