package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 98du on 2020/8/13.
 */
@ApiModel("视频的配乐信息")
@Data
public class BackgroundMusic{

    @ApiModelProperty("音乐名称")
    private String name;

    @ApiModelProperty("音乐歌手")
    private String singer;
}