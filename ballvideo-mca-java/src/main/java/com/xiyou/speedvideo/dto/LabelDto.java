package com.xiyou.speedvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 百度解析的视频标签结果
 * @Author zhangbinglin
 * @Date 2021/1/28 18:25
 **/
@Data
public class LabelDto {

    @ApiModelProperty("300：人物，400：场景，500：知识图谱")
    private Integer type;

    @ApiModelProperty("标签值")
    private String value;

    @ApiModelProperty("分析结果项的置信度，0~100的浮点数")
    private Double score;
}
