package com.miguan.bigdata.entity.npush;

import lombok.Data;

import java.util.Date;

@Data
public class PushDataIterConf {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Integer state;
    private Integer projectType;
    private String appPackage;
    private String usrActCnt;
    private String clickRatio;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;

}
