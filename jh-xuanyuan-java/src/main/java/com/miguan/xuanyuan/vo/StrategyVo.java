package com.miguan.xuanyuan.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel("策略分组详情")
@Data
public class StrategyVo {

    @ApiModelProperty("策略id")
    private Long strategyId;

    @ApiModelProperty("分组类型")
    private Integer type;

    @ApiModelProperty("ab分组id")
    private Long abItemId;

    @ApiModelProperty("ab实验占比")
    private Integer abRate;

    @ApiModelProperty("排序类型")
    private Integer sortType;

    @ApiModelProperty("自定义字段")
    private List<String> customField;

    @ApiModelProperty("代码位列表")
    private List<StrategyCodeVo> adCodeList;


    public List<String> convertCustomField(String customField) {
        List<String> customFieldList = new ArrayList<>();
        if (StringUtils.isNotEmpty(customField)) {
            JSONArray array = JSON.parseArray(customField);
            customFieldList = array.toJavaList(String.class);
        }
        return customFieldList;
    }
}
