package com.miguan.advert.domain.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("表信息")
@Data
public class TableInfo {
    private String columnName; //列名，英文名称
    private String dataType;   //数据类型
    private String columnComment; //备注
    private String columnKey;
    private String extra;


}
