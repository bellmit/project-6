package com.miguan.idmapping.vo;

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
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    private String  createTime;

}
