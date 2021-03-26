package com.miguan.recommend.entity.mongo;

import com.miguan.recommend.common.constants.MongoConstants;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 视频向量接口log
 */
@Data
@Document(MongoConstants.video_embedding_log)
public class VideoEmbeddingLog {

    public VideoEmbeddingLog() {
    }

    public VideoEmbeddingLog(Integer video_id, String video_url, Integer video_type, Integer status, String create_time) {
        this.video_id = video_id;
        this.video_url = video_url;
        this.video_type = video_type;
        this.status = status;
        this.create_time = create_time;
    }

    //视频ID
    private Integer video_id;

    //视频url
    private String video_url;

    //视频类别ID
    private Integer video_type;

    //接口调用结果，1成功/0失败
    private Integer status;

    //创建时间
    private String create_time;
}
