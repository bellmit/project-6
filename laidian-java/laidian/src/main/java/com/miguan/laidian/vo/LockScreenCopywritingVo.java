package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("锁屏文案表实体")
public class LockScreenCopywritingVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("文案")
    private String title;

    @ApiModelProperty("类型:url-链接,noUrl-无链接,appStart-启动app,smallVideo-小视频,laidian-来电秀")
    private String type;

    @ApiModelProperty("类型对应的值")
    private String typeValue;

    @ApiModelProperty("生效日期：当天日期或者-1（无限制）")
    private String effectiveDate;

    @ApiModelProperty("点击数")
    private String clickNums;

}
