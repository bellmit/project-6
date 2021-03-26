package com.miguan.advert.domain.vo.result;

import com.cgcg.context.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @program: advert-java
 * @description: 实验组数组
 * @author: suhj
 * @create: 2020-09-25 20:12
 **/
@ApiModel("实验组保存入参Vo")
@Data
public class TestInVo {
    @ApiModelProperty("实验组id")
    private Integer id;

    @ApiModelProperty("算法：1-手动排序；2-手动配比")
    private Integer computer;

    @ApiModelProperty("开启实验 ,0 : 关闭, 1：开启")
    private Integer openStatus;

    @ApiModelProperty("首次加载位置")
    private String abFirstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private String abSecondLoadPosition;

    @ApiModelProperty("banner广告展示次数限制")
    private String abMaxShowNum;

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

    @ApiModelProperty("关联的代码位策略列表")
    private List<RelaArrVo> relaArr;

    @ApiModel("实验组与配置关系Vo")
    @Data
    public static class RelaArrVo {
        @ApiModelProperty("代码位ID")
        private Integer code_id;

        @ApiModelProperty("配比序号")
        private String number;

        @ApiModelProperty("配比率（%）")
        private String matching;

        @ApiModelProperty("排序序号")
        private String order;
    }
}

