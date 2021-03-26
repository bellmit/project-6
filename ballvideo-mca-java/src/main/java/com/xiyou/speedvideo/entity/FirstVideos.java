package com.xiyou.speedvideo.entity;

import lombok.Data;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 4:25 下午
 */
@Data
public class FirstVideos {
    private Integer id;
    private Integer catId;
    private String title;
    private String bsyUrl;
    private Integer watchCount;
    private String videoTime;
}
