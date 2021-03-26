package com.miguan.laidian.vo;

import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.entity.ActActivityConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Data
@ApiModel("活动页面返回实体信息")
public class ActivityPageInfoVo {
    @ApiModelProperty("活动信息")
    private ActActivity actActivity;
    @ApiModelProperty("转盘配置信息")
    private List<ActActivityConfig> activityConfigs;
    @ApiModelProperty("碎片商城商品列表")
    private List<ActivityConfigVo> activityProducts;
    @ApiModelProperty("剩余抽奖次数")
    private Integer userDrawNum;
}
