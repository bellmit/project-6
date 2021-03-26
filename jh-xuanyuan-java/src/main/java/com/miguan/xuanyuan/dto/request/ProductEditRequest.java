package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>
 * 产品编辑请求体
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-23
 */
@Data
@ApiModel(value="编辑产品参数请求体", description="产品编辑请求")
public class ProductEditRequest {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "产品名称")
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 60,message = "产品名称不能超过60个字符")
    private String productName;

    @ApiModelProperty(value = "状态，1正常，0无效")
    private Integer status = XyConstant.STATUS_NORMAL;


    public void check() throws ValidateException {
    }

}
