package com.miguan.bigdata.entity.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-24 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppDeviceVo {
    private String distinct_id;
    private String package_name;
    private String channel;
    /**
     * 迭代推推送状态 0 关闭，1开启
     */
    private Integer npush_state;
    /**
     * 激活日期：yyyyMMdd
     */
    private Integer dt;
    /**
     * 推送渠道：-1 无 1 友盟 2 华为 3 vivo 4 oppo 5 小米 int
     */
    private Integer npush_channel;
    /**
     * 最后一次登录日期：yyyyMMdd
     */
    private Integer last_visit_date;
    /**
     * 创建时间
     */
    private String  createTime;

}
