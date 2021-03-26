package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("用户标签分实体")
@Entity(name = "user_label_grade")
@Data
public class UserLabelGrade {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("设备ID")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty("分类ID")
    @Column(name = "cat_id")
    private Long catId;

    @ApiModelProperty("标签分")
    @Column(name = "cat_grade")
    private Double catGrade;
    public UserLabelGrade() {}
    public UserLabelGrade(long id, String deviceId, long catId, double catGrade) {
        this.id = id;
        this.deviceId = deviceId;
        this.catId = catId;
        this.catGrade = catGrade;
    }
}
