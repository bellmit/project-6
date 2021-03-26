package com.miguan.reportview.service;

import com.miguan.reportview.dto.HeadFieldDto;

import java.util.List;

/**
 * @Description 报表字段service
 * @Author zhangbinglin
 * @Date 2020/9/8 10:13
 **/
public interface HeadFieldService {
    /**
     * 根据报表code查询字段信息
     * @param tableCode 报表code
     * @return
     */
    List<HeadFieldDto> findByTableCode(String tableCode);

    /**
     * 根据id修改字段的显示和隐藏
     * @param listStr list字符串(包含id和isShow字段值，多个的时候逗号分隔),格式为id@isShow,例如：20@-1,21@1"
     */
    void modifyFieldShow(String listStr);

    void changeHeadFieldInfo(int type, Integer id, String tableCode, String tableName, String fieldCode, String fieldName, Integer fieldType,
                             Integer showType, String fieldRemark, Integer isShow, Integer sort);

}
