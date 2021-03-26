package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 视频分类实体
 *
 * @Author xy.chen
 * @Date 2019/7/8
 **/
@Entity(name="videos_cat")
@Data
@ApiModel("视频分类实体")
public class VideosCat  implements Serializable {

    private static final long serialVersionUID = -2381963330799295855L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("主键")
    private Long id;

    @Column(name = "name")
    @ApiModelProperty("分类名称")
    private String name;

    @Column(name = "sort")
    @ApiModelProperty("排序")
    private Long sort;

    @Column(name = "type")
    @ApiModelProperty("类别：1为安卓，2为iOS")
    private Long type;

    @Column(name = "status")
    @ApiModelProperty("状态:1=启动,-1=禁用")
    private Long status;
}
