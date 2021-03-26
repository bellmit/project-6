package com.miguan.reportview.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YesterDayUserKeepDto {

    private Integer dd;
    private String package_name;
    private String app_version;
    private String change_channel;
    private String father_channel;
    private String distinct_id;
    private Integer is_new_app;
    private Integer user;
    private Integer luser;
    private Integer sd;

}
