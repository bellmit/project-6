package com.miguan.reportview.entity;

import lombok.Data;

import java.util.Date;

/**
 * clickhouse表
 * 对应mongoDB埋点中的xy_video_playover
 */
@Data
public class XyVideoPlayover {
    String uuid;
    String package_name;
    String app_version;
    String channel;
    String change_channel;
    Integer is_new;
    Integer is_new_app;
    String model;
    String distinct_id;
    Date receive_time;
    Date creat_time;
    String country;
    String province;
    String city;
    Integer video_id;
    Integer catid;
    Integer video_time;
    Integer play_time;
    Integer play_time_r;
}
