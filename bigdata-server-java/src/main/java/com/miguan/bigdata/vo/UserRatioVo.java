package com.miguan.bigdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRatioVo {

    @ApiModelProperty("时间段，例如：00:00(0点到0:30间)，12:30(12点到12:30间)")
    private String timeSlot;

    @ApiModelProperty("日活用户数")
    private Integer activeUser;
}
