package com.miguan.laidian.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 公共基类
 * @Author shixh
 * @Date 2019/8/29
 **/
@MappedSuperclass
@Data
public class BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JsonFormat
    @Column(name = "created_at")
    private Date createDate;

    @JsonFormat
    @Column(name = "updated_at")
    private Date updateDate;

}
