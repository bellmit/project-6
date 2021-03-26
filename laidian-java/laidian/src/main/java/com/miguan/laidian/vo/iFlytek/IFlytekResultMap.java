package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 98du on 2020/8/14.
 */
@Data
@ApiModel("讯飞接口返回结果")
public class IFlytekResultMap {

    @ApiModelProperty("返回码")
    private String retcode;

    @ApiModelProperty("返回描述")
    private String retdesc;

    @ApiModelProperty("总条数")
    private String total;

    @ApiModelProperty("是否还有更多：1-有更多，0-没有更多")
    private String more;

    @ApiModelProperty("页码，翻页调用需要回传")
    private String px;

    @ApiModelProperty("返回数据")
    private Object data;
}
