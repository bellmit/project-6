package com.miguan.ballvideo.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 公共基类
 * @Author laiyd
 * @Date 2019/11/05
 **/
@MappedSuperclass
@Data
public class BaseInfoModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JsonFormat
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat
    @Column(name = "update_time")
    private Date updateTime;

}
