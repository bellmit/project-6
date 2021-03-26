package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.dto.HeadFieldDto;
import com.miguan.reportview.entity.AdAdvertCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 报表字段mapper
 */
public interface HeadFieldMapper extends BaseMapper{

    List<HeadFieldDto> findByTableCode(@Param("tableCode") String tableCode);

    /**
     * 根据id修改字段的显示和隐藏
     * @param id
     * @param isShow
     */
    void modifyShowById(@Param("id") Integer id, @Param("isShow") Integer isShow);

    /**
     * 根据id修改字段信息
     * @param params
     */
    void modifyHeadFieldInfo(Map<String, Object> params);

    void addHeadFieldInfo(Map<String, Object> params);

    void deleteHeadFieldInfo(Map<String, Object> params);
}
