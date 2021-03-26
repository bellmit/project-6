package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**ecpm表
 * @Author suhj
 * @Date 2020/8/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAdvertEcpmVo {
    //ID
    private String id;
    //计划ID
    private String planId;
    //创意ID
    private String designId;
    //代码位
    private String code;
    //ecpm-给第三方代码位
    private double ecpm;
    //总收益
    private double income;
    //有效曝光次数
    private long exposure;
    //有效点击次数
    private long click;

    private Date createdAt;

    private Date updatedAt;
}
