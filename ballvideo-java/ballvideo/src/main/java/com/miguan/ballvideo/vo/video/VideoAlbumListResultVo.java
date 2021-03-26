package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Data
@ApiModel("专辑视频列表Vo")
public class VideoAlbumListResultVo {

    @ApiModelProperty("专辑视频与广告信息")
    private List<VideosAlbumInfoVo>  albumData;

    @ApiModelProperty("分页信息")
    private Pageable page;

}
