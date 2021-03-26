package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 首页参数信息vo
 * @Author zhangbinglin
 * @Date 2020/6/3 18:15
 **/
@Data
public class FirstPageParamVo {

    @ApiModelProperty("查询审核通过最新视频的创建时间戳")
    private Long newVideosTime;
}
