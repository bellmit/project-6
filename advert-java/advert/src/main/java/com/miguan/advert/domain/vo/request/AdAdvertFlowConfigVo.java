package com.miguan.advert.domain.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: advert-java
 * @create: 2020-09-27 13:46
 **/
@ApiModel("流量分组配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdAdvertFlowConfigVo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("AB实验ID")
    private String abFlowId;

    @ApiModelProperty("测试组状态：0-关闭，1开启")
    private Integer testState;

}
