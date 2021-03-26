package com.xiyou.speedvideo.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @Description 视频向量对应的视频标签
 * @Author zhangbinglin
 * @Date 2021/1/29 17:41
 **/
@Document(collection = "video_paddle_tag")
@Data
public class VideoPaddleTag {

    //视频id
    private Integer video_id;

    //视频url
    private String url;

    //标签信息
    private String full_label;
}
