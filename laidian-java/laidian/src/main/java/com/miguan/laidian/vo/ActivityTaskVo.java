package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@ApiModel("玩转任务")
@Data
public class ActivityTaskVo {
    @ApiModelProperty("类型1签到 2成功设置来电秀 3成功设置来电铃声 4观看视频 5分享活动")
    private Integer type;
    @ApiModelProperty("状态 0未完成 1已完成")
    private Integer state;
}
