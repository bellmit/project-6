package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "预估参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictDto {

    @ApiModelProperty(value = "用户标识", hidden = true)
    private String device_id;
    @ApiModelProperty(value = "渠道", hidden = true)
    private String channel;
    @ApiModelProperty(value = "app的包名", hidden = true)
    private String package_name;
    @ApiModelProperty(value = "机型", hidden = true)
    private String model;
    @ApiModelProperty(value = "操作系统 android/ios", hidden = true)
    private String os;
    @ApiModelProperty(value = "是否首次访问该app, true/false", hidden = true)
    private boolean is_first_app;
    @ApiModelProperty(value = "是否新用户, true/false", hidden = true)
    private boolean is_first;
    @ApiModelProperty(value = "用户特征", hidden = true)
    private UserFeature userFeature;
}
