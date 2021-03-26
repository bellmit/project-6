package com.miguan.recommend.bo;

import lombok.Data;

@Data
public class VideoInitDto {

    public final static String init_video = "init_video";
    public final static String wash_incentive_video = "wash_incentive_video";
    public final static String wash_hotpots_video = "wash_hotpots_video";

    public final static String video_paddle_tag_top5 = "video_paddle_tag_top5";
    public final static String es_video_title_init = "es_video_title_init";

    private String type;
    private int skip;
    private int size;

    public VideoInitDto(){

    }

    public VideoInitDto(String type, int skip, int size){
        this.type = type;
        this.skip = skip;
        this.size = size;
    }

}
