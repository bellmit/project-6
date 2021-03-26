package com.miguan.advert.domain.vo.result;


import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.Date;

@Data
public class AdOperationLogVo {
    private Long id;
    private String operation_user;  //操作者
    private String path_url;        //路由
    private String path_name;       //路由中文名
    private Integer type;           //操作类型 1 增 2 删 3 改
    private String c_content;  //操作内容
    private String code_id;         //广告位
    private Date created_at;
    private Date updated_at;
    private Object[] change_content;  //操作内容
}
