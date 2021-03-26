package com.miguan.reportview.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.reportview.dto.HeadFieldDto;
import com.miguan.reportview.mapper.HeadFieldMapper;
import com.miguan.reportview.service.HeadFieldService;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 报表字段service
 * @Author zhangbinglin
 * @Date 2020/9/8 10:14
 **/
@Service
public class HeadFieldServiceImpl implements HeadFieldService {

    @Resource
    private HeadFieldMapper headFieldMapper;

    @Override
    public List<HeadFieldDto> findByTableCode(String tableCode) {
        return headFieldMapper.findByTableCode(tableCode);
    }

    @Override
    public void modifyFieldShow(String listStr) {
        if(StringUtil.isBlank(listStr)) {
            return;
        }
        String[] list = listStr.split(",");
        for(int i=0;i<list.length;i++) {
            String[] sonList = list[i].split("@");
            if(sonList.length < 2) {
                continue;
            }
            headFieldMapper.modifyShowById(Integer.parseInt(sonList[0]), Integer.parseInt(sonList[1]));
        }
    }

    public void changeHeadFieldInfo(int type, Integer id, String tableCode, String tableName, String fieldCode, String fieldName, Integer fieldType,
                                    Integer showType, String fieldRemark, Integer isShow, Integer sort) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("tableCode", tableCode);
        params.put("tableName", tableName);
        params.put("fieldCode", fieldCode);
        params.put("fieldName", fieldName);
        params.put("fieldType", fieldType);
        params.put("showType", showType);
        params.put("fieldRemark", fieldRemark);
        params.put("isShow", isShow);
        params.put("sort", sort);
        if(type == 0) {
            headFieldMapper.addHeadFieldInfo(params);
        } else if(type == 1) {
            headFieldMapper.modifyHeadFieldInfo(params);
        } else if(type == 2) {
            headFieldMapper.deleteHeadFieldInfo(params);
        }
    }

}
