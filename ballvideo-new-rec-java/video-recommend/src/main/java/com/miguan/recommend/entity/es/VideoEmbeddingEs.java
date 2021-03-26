package com.miguan.recommend.entity.es;

import lombok.Data;

/**
 * @Description ES视频向量
 **/
@Data
public class VideoEmbeddingEs {
    //uuid
    private String video_id;

    //视频向量
    private String vector;

    //视频url
    private String video_url;
}
