package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.entity.XyPlat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 广告平台
 * @Date 2021/1/21
 **/
public interface XyPlatMapper extends BaseMapper<XyPlat> {
    int judgeExistKey(@Param("platKey") String platKey, @Param("id") Long id);
    List<XyPlat> findList();
    XyPlat getPlatDataByPlatKey(String platKey);
    List<XyPlat> findByAdType(@Param("adType") String adType);
}
