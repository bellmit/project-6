package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@ApiModel("短信返回结果")
@Slf4j
public final class SmsResultMap{

    @ApiModelProperty("编号")
    private int result;
    @ApiModelProperty("返回数据")
    private String msgid;
    @ApiModelProperty("提示信息")
    private String ts;

    public SmsResultMap() {
    }


}
