package com.miguan.report.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("使用时长数据实体")
@Data
public class AppUseTimeVo {

    @ApiModelProperty("数据类型：1 汇总数据, 2 茜柚视频-Android, 3 果果视频_Android, 4")
    private Integer dataType;

    @ApiModelProperty("数据日期")
    private String day;

    @ApiModelProperty("使用时长字符串(hh:mm:ss)")
    private String useTimeStr;

}
