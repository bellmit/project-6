package com.miguan.ballvideo.vo.video;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.FirstVideos;
import lombok.Data;

@Data
public class FirstVideosNewVo{

	private AdvertVo advertVo;

	private Page<FirstVideos> page;

}
