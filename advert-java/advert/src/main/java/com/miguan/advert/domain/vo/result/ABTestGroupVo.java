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
 * @description: AB对照组分组列表
 * @author: suhj
 * @create: 2020-09-25 20:12
 **/
@ApiModel("AB对照组分组列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ABTestGroupVo implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("实验组ID")
    private String ab_test_id;

    @ApiModelProperty("百分比")
    private String percentage;

    @ApiModelProperty("算法：1-手动配比，2-手动排序")
    private Integer computer;

    @ApiModelProperty("分组类型：0:默认分组, 1：对照组, 2:测试组")
    private Integer type;

    @ApiModelProperty("广告配置代码位信息")
    private List<AdvCodeInfoVo> posCodeLstVos;

    @ApiModelProperty("测试组名称")
    private String name;

    public ABTestGroupVo(Integer id, String ab_test_id, String percentage, Integer computer, Integer type, List<AdvCodeInfoVo> posCodeLstVos, String name) {
        this.id = id;
        this.ab_test_id = ab_test_id;
        this.percentage = percentage;
        this.computer = computer;
        this.type = type;
        this.posCodeLstVos = posCodeLstVos;
        this.name = name;
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
