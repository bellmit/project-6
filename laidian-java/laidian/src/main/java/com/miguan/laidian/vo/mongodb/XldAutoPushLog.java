package com.miguan.laidian.vo.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 自动推送日志
 */
@Setter
@Getter
@Document("xld_auto_push_log")
@NoArgsConstructor
@AllArgsConstructor
public class XldAutoPushLog {
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
