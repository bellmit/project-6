package com.miguan.xuanyuan.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.xuanyuan.common.log.annotation.UpdateIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "created_at")
    @UpdateIgnore
    private Date createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "updated_at")
    @UpdateIgnore
    private Date updatedAt;

}
