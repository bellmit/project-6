package com.miguan.ballvideo.vo.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 视频权重表
 * @author zhongli
 * @date 2020-08-11 
 *
 */
@Setter
@Getter
@Document("auto_push_log")
@NoArgsConstructor
@AllArgsConstructor
public class AutoPushLog {

    private Long push_id;
    private String title;
    private String content;
    private String distinct_id;
    private String video_id;
    private String app_package;
    private String type; //0：老推送 1：新推送
    private String push_channel;
    private String is_succ; //是否推送成功：0：失败 1：成功
    private Date create_at = new Date();
}
