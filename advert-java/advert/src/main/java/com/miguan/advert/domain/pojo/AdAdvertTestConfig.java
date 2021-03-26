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
@ApiModel("实验分组配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdAdvertTestConfig {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("流量分组ID")
    private String flow_id;

    @ApiModelProperty("实验分组ID")
    private String ab_test_id;

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

    public AdAdvertTestConfig(Integer id, String flow_id, String ab_test_id, Integer computer, Integer type, Date created_at, Date updated_at, Integer state) {
        this.id = id;
        this.flow_id = flow_id;
        this.ab_test_id = ab_test_id;
        this.computer = computer;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.state = state;
    }


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
