package com.miguan.ballvideo.vo.video;

import lombok.Data;

import java.util.List;

/**
 * @Author shixh
 * @Date 2020/1/12
 **/
@Data
public class VideoGatherVo {
    private Long id;
    private String title;
    private int count;
    private List<Videos161Vo> searchData;
}
