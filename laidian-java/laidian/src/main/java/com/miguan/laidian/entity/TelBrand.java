package com.miguan.laidian.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TelBrand {

    private Long id;

    private String telBrand;

    private String telKey;

    private Date createTime;

    private Date updateTime;

}