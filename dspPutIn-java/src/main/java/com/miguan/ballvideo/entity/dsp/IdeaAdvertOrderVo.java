package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**订单表
 * @Author suhj
 * @Date 2020/8/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAdvertOrderVo {
    //ID
    private String id;
    //设备号
    private String device;
    //广告计划ID
    private String plan_id;
    //创意ID
    private String design_id;
    //订单ID
    private String order_id;
    //出价
    private double price;

    private Date created_at;

    private Date updated_at;
}
