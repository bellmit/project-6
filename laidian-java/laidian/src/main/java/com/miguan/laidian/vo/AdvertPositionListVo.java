package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 广告位置VO 2.6及以上版本使用
 * @author laiyd
 * @date 2020/05/09
 **/
@ApiModel("广告位置VO(V2.6)")
@Data
public class AdvertPositionListVo {

    @ApiModelProperty("广告位置信息")
    private List<AdvertPositionVo> advertPositionVoList;

    @ApiModelProperty("来电分类解锁广告的天数配置")
    private Integer laidianUnlockDays;

    @ApiModelProperty("来电强制广告的天数配置")
    private Integer laidianForceDays;

    @ApiModelProperty("来电详情退出广告位每天上限")
    private Integer callDetailsQuitLimit;

}
