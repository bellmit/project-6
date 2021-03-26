package com.miguan.ballvideo.vo.video;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.SmallVideosVo;
import lombok.Data;

/**
 * @Author shixh
 * @Date 2019/9/24
 **/
@Data
public class SmallVideosNewVo {

    private AdvertVo advertVo;
    private Page<SmallVideosVo> page;
}
