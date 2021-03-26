package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.response.AdvertAppSimpleRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 应用
 * @Date 2020/11/20 16:44
 **/
public interface AdvertAppMapper {
    List<AdvertAppSimpleRes> getAppList(@Param("materialShape") String materialShape,@Param("materialType") String materialType);
}
