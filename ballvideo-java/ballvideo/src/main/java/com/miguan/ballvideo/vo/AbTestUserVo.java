package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("AB测试用户结果实体")
@Data
public class AbTestUserVo {

    @ApiModelProperty("小视频红点标记")
    private String smallVideoBadge;

    @ApiModelProperty("小视频默认详情页")
    private String smallVideoDefault;

    @ApiModelProperty("app启动进入页")
    private String appStartDefault;

    @ApiModelProperty("首页加载视频数量")
    private String indexVideoShow;
}
