package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 分享VO
 * @Author zhangbinglin
 * @Date 2019/7/10 11:46
 **/
@Data
@ApiModel("分享VO")
public class ShareVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分享标题")
    private String shareTitle;

    @ApiModelProperty("分享内容")
    private String shareContent;

    @ApiModelProperty("分享链接")
    private String shareUrl;

    @ApiModelProperty("logo链接")
    private String logoUrl;

}
