package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "app_use_time")
public class AppUseTime {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * APP类型：1 视频类, 2 来电类
     */
    @Column(name = "app_type")
    private Integer appType;
    /**
     * 数据类型：1 汇总数据, 2 茜柚视频-Android, 3 果果视频_Android, 4 炫来电-Android
     */
    @Column(name = "data_type")
    private Integer dataType;
    /**
     * 数据日期
     */
    @Column(name = "use_day")
    private String useDay;
    /**
     * 使用时长(秒)
     */
    @Column(name = "use_time")
    private Integer useTime;
    /**
     * 使用时长字符串(hh:mm:ss)
     */
    @Column(name = "use_time_str")
    private String useTimeStr;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;
}
