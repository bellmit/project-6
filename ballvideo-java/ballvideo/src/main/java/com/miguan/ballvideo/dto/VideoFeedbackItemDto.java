package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description 视频反馈dto
 * @Author zhangbinglin
 * @Date 2020/8/3 15:59
 **/
@Data
public class VideoFeedbackItemDto {

    @ApiModelProperty("反馈类型")
    private List<Map<String, Object>> feedBackType;

    @ApiModelProperty("反馈明细")
    private List<Map<String, Object>> feedBackDetail;

}
