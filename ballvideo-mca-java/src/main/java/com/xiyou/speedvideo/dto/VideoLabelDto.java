package com.xiyou.speedvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 百度解析的视频标签结果
 * @Author zhangbinglin
 * @Date 2021/1/28 18:25
 **/
@Data
public class VideoLabelDto {

    @ApiModelProperty("视频id")
    private Integer videoId;

    @ApiModelProperty("结果类型：0--标签解析中，1--标签解析完成")
    private Integer resultType;

    @ApiModelProperty("结果说明")
    private String result;

    @ApiModelProperty("标签集")
    private List<LabelDto> tags;

    @ApiModelProperty("来源，内容:CONTENT, 算法:ALGORITHM 百度AI:BAIDUAI")
    private String source;

}
