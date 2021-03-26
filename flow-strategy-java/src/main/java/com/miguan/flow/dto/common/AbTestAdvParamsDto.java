package com.miguan.flow.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("AB实验平台入参实体")
public class AbTestAdvParamsDto {

    //对应head的abTestId
    //格式：[{"exp_key":"ad_exp_134_0","group_id":987},{"exp_key":"ad_exp_690_8","group_id":826}]。其中exp_key： ad_exp_广告组id_后面这个无所谓，group_id是对照组或测试组id
    @ApiModelProperty("实验分组Id")
    private String abTestId;

    //对应head的ab-exp
    //格式：实验id-对照组或测试组id。例如：53-115,86-184,91-194
    @ApiModelProperty("实验id")
    private String abExp;
}
