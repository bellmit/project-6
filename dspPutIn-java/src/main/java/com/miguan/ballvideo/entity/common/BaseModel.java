package com.miguan.ballvideo.entity.common;

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
    private Date createDate;//之前公共字段都是根据PHP后台代码生成，这里保持一致（created_at）。

    @JsonFormat
    @Column(name = "updated_at")
    private Date updateDate;//之前公共字段都是根据PHP后台代码生成，这里保持一致（updated_at）。

}
