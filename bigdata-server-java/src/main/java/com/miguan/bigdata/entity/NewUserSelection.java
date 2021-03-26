package com.miguan.bigdata.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NewUserSelection {

    private Integer id;
    private Integer tagId;
    private Integer videoId;
    private Integer showCount;
    private Integer playCount;
    private Double vplayRate;
    private Integer vplayCount;
    private Double allPlayRate;
    private Integer sort;
    private Date createdAt;
    private Date updatedAt;

}
