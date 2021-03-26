package com.miguan.advert.domain.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: advert-java
 * @description: 广告流量分组配置表
 * @author: suhj
 * @create: 2020-09-27 13:46
 **/
@ApiModel("流量分组配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdAdvertFlowConfig {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("流量分组名称")
    private String name;

    @ApiModelProperty("广告位置ID")
    private Integer position_id;

    @ApiModelProperty("AB实验ID")
    private String ab_flow_id;

    @ApiModelProperty("分组类型：1：默认分组,2:手动分组")
    private Integer type;

    @ApiModelProperty("测试组状态：0-关闭，1开启")
    private Integer test_state;

    @ApiModelProperty("创建日期")
    private Date created_at;

    @ApiModelProperty("修改日期")
    private Date updated_at;

    @ApiModelProperty("状态：0-关闭，1开启")
    private Integer state;

    @ApiModelProperty("实验码")
    private String exp_code;

    @ApiModelProperty("运行时间")
    private String pub_time;

    @ApiModelProperty("AB实验ID 查询返回使用")
    private String abFlowId;

    @ApiModelProperty("广告位置ID 查询返回使用")
    private Integer positionId;

    @ApiModelProperty("AB运行状态,0:未开启,1:开启,2:关闭,与AB平台保持一致.")
    private Integer status;
    @ApiModelProperty("实验开启状态,0：关闭,1:开启")
    private Integer open_status;


    public AdAdvertFlowConfig(Integer id, String name, Integer position_id, String ab_flow_id, Integer type, Integer test_state, Date created_at, Date updated_at, Integer state) {
        this.id = id;
        this.name = name;
        this.position_id = position_id;
        this.ab_flow_id = ab_flow_id;
        this.type = type;
        this.test_state = test_state;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.state = state;
    }


    public AdAdvertFlowConfig(Integer id, String name, Integer position_id, String ab_flow_id, Integer type, Integer test_state, Date created_at, Date updated_at, Integer state,String exp_code,String pub_time,Integer status) {
        this.id = id;
        this.name = name;
        this.position_id = position_id;
        this.ab_flow_id = ab_flow_id;
        this.type = type;
        this.test_state = test_state;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.state = state;
        this.exp_code = exp_code;
        this.pub_time = pub_time;
        this.status = status;
    }
}
