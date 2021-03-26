package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@ApiModel("白山云预热接口返回日志")
@Entity(name = "bsy_log")
public class BsyLog {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id",columnDefinition="bigint COMMENT '主键'")
    private Long id;

    @Column(name = "code",columnDefinition="int(10) COMMENT 'code'")
    private int code;

    @ApiModelProperty("message")
    @Column(name = "message",columnDefinition="varchar(100) COMMENT 'message'")
    private String message;

    @ApiModelProperty("视频ids")
    @Column(name = "ids",columnDefinition="varchar(4000) COMMENT '视频ids'")
    private String ids;

    @ApiModelProperty("推送数")
    @Column(name = "send_count",columnDefinition="int(10) COMMENT '推送数量'")
    private Integer sendCount;

    @ApiModelProperty("任务id")
    @Column(name = "task_id",columnDefinition="varchar(20) COMMENT '任务id'")
    private String taskId;

    @ApiModelProperty("数量")
    @Column(name = "count",columnDefinition="int(10) COMMENT '数量'")
    private Integer count;

    @ApiModelProperty("创建时间")
    @Column(name = "create_date",columnDefinition="datetime COMMENT '创建时间'")
    @CreationTimestamp
    private Date createDate;



}
