package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: advert-java
 * @description: AB流量分组列表信息
 * @author: suhj
 * @create: 2020-09-25 20:12
 **/
@ApiModel("AB流量分组列表信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ABFlowGroupVo implements Serializable {

    @ApiModelProperty("流量分组id")
    private Integer id;

    @ApiModelProperty("流量分组名称")
    private String name;

    @ApiModelProperty("渠道名称")
    private String channel_name;

    @ApiModelProperty("分组类型：1：默认分组,2:手动分组")
    private Integer type;

    @ApiModelProperty("测试组状态：0-关闭，1开启")
    private Integer test_state;

    @ApiModelProperty("应用版本")
    private String app_version;

    @ApiModelProperty("实验分组ID")
    private String ab_flow_id;

    @ApiModelProperty("默认广告配置代码位信息")
    private List<AdvCodeInfoVo> defaultPosCodeLstVos;

    @ApiModelProperty("AB对照组分组列表")
    private List<ABTestGroupVo> abTestGroupVoList;

    @ApiModelProperty("发布时间")
    private String pubTime;

    @ApiModelProperty("状态")
    private Integer Status;
    @ApiModelProperty("手机类型")
    private Integer mobile_type;

    @ApiModelProperty("开启状态 0关闭, 1开启")
    private Integer open_status;


    //@ApiModelProperty("默认实验分组列表")
    //private List<ABTestGroupVo> defaultTestGroupVoList;
}
