package com.miguan.laidian.entity;

import io.swagger.annotations.*;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity(name = "act_activity_config")
@ApiModel("活动商品配置表")
public class ActActivityConfig {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "活动id")
    @Column(name = "activity_id")
    private Long activityId;

    @ApiModelProperty(value = "奖品名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "奖品价格")
    @Column(name = "price")
    private BigDecimal price;

    @ApiModelProperty(value = "奖品成本")
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "奖品图片")
    @Column(name = "pic")
    private String pic;

    @ApiModelProperty(value = "奖品虚拟份数")
    @Column(name = "prize_virtual_num")
    private Integer prizeVirtualNum;

    @ApiModelProperty(value = "奖品真实份数")
    @Column(name = "prize_real_num")
    private Integer prizeRealNum;

    @ApiModelProperty(value = "奖品已领取数量")
    @Column(name = "prize_recive_num")
    private Integer prizeReciveNum;

    @ApiModelProperty(value = "碎片达标数")
    @Column(name = "debris_reach_num")
    private Integer debrisReachNum;

    @ApiModelProperty(value = "碎片中奖概率")
    @Column(name = "debris_draw_rate")
    private Integer debrisDrawRate;

    @ApiModelProperty(value = "最后一个碎片中奖概率")
    @Column(name = "last_debris_draw_rate")
    private Integer lastDebrisDrawRate;

    @ApiModelProperty(value = "商城排序 升序")
    @Column(name = "sort")
    private Integer sort;

    @ApiModelProperty(value = "转盘排序 升序")
    @Column(name = "rotary_sort")
    private Integer rotarySort;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Date updatedAt;
}