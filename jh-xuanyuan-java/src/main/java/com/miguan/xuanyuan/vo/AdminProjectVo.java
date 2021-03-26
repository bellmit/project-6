package com.miguan.xuanyuan.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目表
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Data
@ApiModel("项目")
public class AdminProjectVo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "项目名称")
    private String name;

    @ApiModelProperty(value = "域名")
    private String host;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "类型")
    private Integer type = 0;

    @ApiModelProperty(value = "是否显示，1显示，0隐藏")
    private Integer isShow;

    @ApiModelProperty(value = "是否存在子节点")
    private Integer hasChildren;


}
