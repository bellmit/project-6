package com.miguan.ballvideo.vo.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document("user_incentive_log")
public class UserIncentiveLog {

    private String distinct_id;
    private String video_id;
    private Integer catid;
    private Date creat_at;
}