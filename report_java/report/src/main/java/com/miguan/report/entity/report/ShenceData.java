package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "shence_data")
public class ShenceData {

    public ShenceData() {
    }

    public ShenceData(String date, Integer appType, Integer appId, String appName, Integer clientId) {
        this.date = date;
        this.appType = appType;
        this.appId = appId;
        this.appName = appName;
        this.clientId = clientId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String date;
    private Integer newUsers;
    private Integer totalUsers;
    private Integer active;
    private Integer appType;
    private Integer appId;
    private String appName;
    private Integer clientId;
    private Date createdAt;
}
