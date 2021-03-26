package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.dto.ab.AbFlowGroupParam;
import com.miguan.xuanyuan.dto.ab.AbItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@ApiModel("ab实验请求体")
@Data
public class AbTestRequest {

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("策略分组id")
    private Long strategyGroupId;

    @ApiModelProperty("实验名称")
    private String expName;

    @ApiModelProperty("包名")
    private String packageName;

    @ApiModelProperty("第三方广告平台key")
    private List<AbItem> flowRate;

    @ApiModelProperty("实验分层")
    private String layerId;

    @ApiModelProperty("渠道类型（废弃）")
    private Integer channelType;

    @ApiModelProperty("渠道操作类型")
    private String channelOperation;

    @ApiModelProperty("作用版本操作类型")
    private String versionOperation;

    @ApiModelProperty("渲染方式")
    private String channelIds;

    @ApiModelProperty("版本类型：1-全部 2-仅限 3-排除（废弃）")
    private Integer versionType;

    @ApiModelProperty("版本，多个逗号隔开")
    private String versionIds;

    @ApiModelProperty("是否为新客")
    private Integer isNew;

}

