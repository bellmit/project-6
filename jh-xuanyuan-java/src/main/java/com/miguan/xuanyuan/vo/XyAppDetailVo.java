package com.miguan.xuanyuan.vo;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("轩辕应用")
@Data
public class XyAppDetailVo extends BaseModel {
    private Long userId;

    private String userName;

    private String identityName;

    private String appName;

    private String appKey;

    private String appSecret;

    private String categoryType;

    private String packageName;

    private String downloadUrl;

    private String icon;

    private Integer clientType;

   private Integer status;

    private Integer isDel;

    private String sha;

    private Long productId;

}
