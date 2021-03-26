package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 视频合集表
 * @Author shixh
 * @Date 2020/1/9
 **/
@Entity(name="video_gather")
@Data
public class VideoGather extends BaseModel{

    public static int RECOMMEND = 1;//推荐
    public static int NO_RECOMMEND = 0;//不推荐

    private String title;//热词内容
    private int state;//1-开启 0-关闭
    private int recommendState;//1-推荐 0-不推荐
    private String baseWeight;//权重
}
