package com.miguan.ballvideo.vo.video;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.FirstVideos;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FirstVideos16Vo {

	private List<AdvertVo> advertVos;

	private List<AdvertCodeVo> advertCodeVos;//V2.5.0广告返回数据

	private Page<FirstVideos> page;

}
