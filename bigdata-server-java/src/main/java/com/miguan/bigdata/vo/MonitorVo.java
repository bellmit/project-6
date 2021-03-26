package com.miguan.bigdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 监测vo
 * @Author zhangbinglin
 * @Date 2020/11/6 9:39
 **/
@Data
public class MonitorVo {

    /**
     * 版本
     */
    private String appVersion;

    /**
     * 监测内容
     */
    private String type;

    /**
     * uuid丢失率
     */
    private Double loseRate;
}
