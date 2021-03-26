package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "hour_data")
public class HourData {

    public HourData(){}

    public HourData(String adId, String hours, Integer showNumber, Integer clickNumber, Date date) {
        this.adId = adId;
        this.hours = hours;
        this.showNumber = showNumber;
        this.clickNumber = clickNumber;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String adId;
    private Date date;
    private String hours;
    private Integer showNumber;
    private Integer clickNumber;
    private Double clickRate;
    private Integer createTime;

}
