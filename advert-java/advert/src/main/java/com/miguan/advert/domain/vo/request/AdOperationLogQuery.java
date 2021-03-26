package com.miguan.advert.domain.vo.request;


import lombok.Data;

import java.util.Date;

@Data
public class AdOperationLogQuery {
    private Integer type;           //操作类型 1 增 2 删 3 改
    private String key;             //查询关键字
    private String path_name;       //查询路径
    private Date s_time;            //开始时间
    private Date e_time;            //结束时间
}
