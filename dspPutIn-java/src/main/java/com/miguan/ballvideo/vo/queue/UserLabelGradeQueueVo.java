package com.miguan.ballvideo.vo.queue;

import lombok.Data;

@Data
public class UserLabelGradeQueueVo {

    private String deviceId;
    private Long catId;
    private Double catGrade;
    private String actionId;

    UserLabelGradeQueueVo(){}
    public UserLabelGradeQueueVo(String deviceId, Long catId, String actionId){
        this.deviceId = deviceId;
        this.catId = catId;
        this.actionId = actionId;
    }
}
