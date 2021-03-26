package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 广告主实体类
 * @Author suhj
 * @Date 2020/8/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAdvertUserVo {
    //广告主id，不传新增，传更新
    private String ad_user_id;
    //广告主名称
    private String name;
    //联系人
    private String link_man;
    //联系人电话
    private String link_phone;
    //联系人邮箱
    private String link_email;
    //备注
    private String remake;
    //行业
    private String type;
}
