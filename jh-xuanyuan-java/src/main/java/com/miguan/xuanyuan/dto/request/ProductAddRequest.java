package com.miguan.xuanyuan.dto.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.enums.PermissionTypeEnum;
import com.miguan.xuanyuan.common.exception.ValidateException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 产品添加请求体
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-23
 */
@Data
@ApiModel(value="添加产品参数请求体", description="产品添加请求")
public class ProductAddRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产品名称")
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 60,message = "产品名称不能超过60个字符")
    private String productName;

    @ApiModelProperty(value = "状态，1正常，0无效")
    private Integer status = XyConstant.STATUS_NORMAL;


    public void check() throws ValidateException {
    }

}
