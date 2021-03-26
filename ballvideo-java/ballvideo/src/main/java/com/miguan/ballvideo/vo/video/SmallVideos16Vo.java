package com.miguan.ballvideo.vo.video;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.SmallVideosVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author shixh
 * @Date 2019/10/22
 **/
@Data
public class SmallVideos16Vo {

    private List<AdvertVo> advertVos;
    private Page<SmallVideosVo> page;

    @ApiModelProperty("V2.5.0广告返回数据")
    List<AdvertCodeVo> advertCodeVos;
}
