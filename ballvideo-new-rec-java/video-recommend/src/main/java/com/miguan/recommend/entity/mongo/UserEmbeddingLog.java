package com.miguan.recommend.entity.mongo;

import com.miguan.recommend.common.constants.MongoConstants;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户向量接口log
 */
@Data
@Document(MongoConstants.user_embedding_log)
public class UserEmbeddingLog {

    public UserEmbeddingLog() {
    }

    public UserEmbeddingLog(String uuid, String distinct_id, String channel, String city, String model, Integer top_type, String appList, Integer status, String embedding, String create_time) {
        this.uuid = uuid;
        this.distinct_id = distinct_id;
        this.channel = channel;
        this.city = city;
        this.model = model;
        this.top_type = top_type;
        this.appList = appList;
        this.status = status;
        this.embedding = embedding;
        this.create_time = create_time;
    }

    //uuid
    private String uuid;

    //distinctId
    private String distinct_id;

    //用户渠道
    private String channel;

    //用户城市
    private String city;

    //用户机型
    private String model;

    //用户观看最多视频类别
    private Integer top_type;

    //用户安装app列表
    private String appList;

    //接口调用结果，1成功/0失败
    private Integer status;

    //接口调用接口向量数组
    private String embedding;

    //创建时间
    private String create_time;
}
