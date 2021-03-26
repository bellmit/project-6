package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 轩辕品牌广告代码位表
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XyPlanPositionCode对象", description="轩辕品牌广告代码位表")
public class XyPlanPositionCode extends Model<XyPlanPositionCode> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "广告位id")
    private Long positionId;

    @ApiModelProperty(value = "代码位，开屏前缀xy1,信息流前缀xy2,插屏前缀xy3，banner前缀xy4")
    private String codeId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
