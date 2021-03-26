package com.miguan.advert.domain.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: advert-java
 **/
@ApiModel("实验分组配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdAdvertTestConfigVo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("流量分组ID")
    private String flowId;

    @ApiModelProperty("实验分组ID")
    private String abTestId;

    @ApiModelProperty("算法：1-手动配比;2-手动排序")
    private Integer computer;

    @ApiModelProperty("分组类型：0:默认分组, 1：对照组, 2:测试组")
    private Integer type;

    @ApiModelProperty("创建日期")
    private Date created_at;

    @ApiModelProperty("修改日期")
    private Date updated_at;

    @ApiModelProperty("状态：0-关闭，1开启")
    private Integer state;


    @ApiModelProperty("首次加载位置")
    private Integer abFirstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private Integer abSecondLoadPosition;

    @ApiModelProperty("banner广告展示次数限制")
    private Integer abMaxShowNum;

    @ApiModelProperty("自定义规则1")
    private String abCustomRule1;

    @ApiModelProperty("自定义规则2")
    private String abCustomRule2;

    @ApiModelProperty("自定义规则3")
    private String abCustomRule3;

    @ApiModelProperty("阶梯广告延迟请求毫秒")
    private String ladderDelayMillis;

    @ApiModelProperty("通跑广告延迟请求毫秒数")
    private String commonDelayMillis;
}
