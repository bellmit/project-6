package com.miguan.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface SelectMapper {

    /**
     * 获取下拉app列表
     *
     * @param appType 类型：1=西柚视频,2=炫来电
     * @return 下拉app列表
     */
    @Select("select id, name from app where app_type = #{appType}")
    List<Map<String, Object>> appList(@Param("appType") Integer appType);

    /**
     * 获取下广告位列表
     *
     * @return 广告位列表
     */
    @Select("select id, total_name name from banner_rule where app_type = #{appType} group by total_name")
    List<Map<String, Object>> bannerPositionList(@Param("appType") Integer appType);

    /**
     * 下拉选项-获取代码为ID列表
     *
     * @param params   关键字
     * @returns
     */
//    List<String> getAdSpaceIdList(@Param("keyword") String keyword, @Param("totalNames") List<String> totalName, @Param("appType") Integer appType);
    List<String> getAdSpaceIdList(Map<String, Object> params);

}
