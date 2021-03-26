package com.miguan.report.dto;

import lombok.Data;

@Data
public class AdIdAndNameDto {
    /**
     * 代码位
     */
    private String adId;
    /**
     * 代码位名称
     */
    private String name;
    /**
     * 代码位总称
     */
    private String totalName;
}
