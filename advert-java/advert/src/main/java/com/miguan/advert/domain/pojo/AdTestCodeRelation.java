package com.miguan.advert.domain.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: advert-java
 * @description: 广告实验组与代码位关联表
 * @author: suhj
 * @create: 2020-09-27 13:46
 **/
@ApiModel("广告实验组与代码位关联表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdTestCodeRelation {


    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("配置ID")
    private String config_id;

    @ApiModelProperty("代码位ID")
    private Integer code_id;

    @ApiModelProperty("配比序号")
    private Integer number;

    @ApiModelProperty("配比率（%）")
    private Integer matching;

    @ApiModelProperty("排序序号")
    private Integer order_num;

    @ApiModelProperty("创建日期")
    private Date created_at;

    @ApiModelProperty("修改日期")
    private Date updated_at;

    @ApiModelProperty("状态：0-关闭，1开启")
    private Integer state;
}
