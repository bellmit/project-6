package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.common.log.annotation.UpdateIgnore;
import com.miguan.xuanyuan.common.validate.annotation.ClientType;
import com.miguan.xuanyuan.common.validate.annotation.ComplexStatus;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel("轩辕应用")
@Data
public class XyApp extends BaseModel {
    @ApiModelProperty("媒体账号id")
    private Long userId;

    @ApiModelProperty("应用名称")
    @NotBlank(message = "应用名称不能为空！")
    @Size(max = 16,message = "应用名称不能超过16个字符！")
    private String appName;

    @ApiModelProperty("appkey")
    @UpdateIgnore
    private String appKey;
    @ApiModelProperty("app密钥")
    @UpdateIgnore
    private String appSecret;

    @ApiModelProperty("应用分类key")
    @NotBlank(message = "请选择应用分类！")
    private String categoryType;

    @ApiModelProperty("包名")
    @NotBlank(message = "包名不能为空！")
    private String packageName;

    @ApiModelProperty("下载链接")
    @NotBlank(message = "下载链接不能为空！")
    @Size(max = 200,message = "下载链接不能超过200个字符！")
    @Pattern(message = "下载链接格式不正确！" , regexp = "^((https|http|ftp|rtsp|mms)?:(/*))?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.[a-z]{2,6})(:[0-9]{1,5})?((/?)|([\\s\\S]*)/?)$")
    private String downloadUrl;

    @ApiModelProperty("app图标")
    @NotBlank(message = "app图标不能为空！")
    private String icon;

    @ApiModelProperty("操作系统，1安卓，2ios")
    @NotNull(message = "操作系统不能为空！")
    @ClientType
    private Integer clientType;

    @ApiModelProperty("状态，0已启用，1待审核，2已禁用")
    @NotNull(message = "状态不能为空！")
    @ComplexStatus
    private Integer status;

    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel;

    @ApiModelProperty("产品id")
    //todo
//    @NotBlank(message = "产品id不能为空！")
    private Long productId;

    @ApiModelProperty("sha值")
    @NotBlank(message = "sha值不能为空！")
    @Size(max = 125,message = "sha值不能超过125个字符！")
    private String sha;

}
