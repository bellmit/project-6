package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * 用户-兴趣标签
 */
@Data
public class LdInterestTagVo {

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("分类id")
    private Integer catId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("软删除，0：删除，1：未删除")
    private Integer isDelete;

    @ApiModelProperty("标签id")
    private Integer tagId;

    @ApiModelProperty("创建时间")
    private Date createdAt;

    @ApiModelProperty("更新时间")
    private Integer updatedAt;
}
