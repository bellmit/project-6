package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "umeng_data")
public class UmengData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String date;
    private Integer useTime;
    private Integer newUsers;
    private Integer totalUsers;
    private Integer active;
    private Integer appType;
    private Integer appId;
    private String appName;
    private Integer clientId;
    private Date createdAt;


}
