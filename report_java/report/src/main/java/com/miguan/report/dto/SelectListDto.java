package com.miguan.report.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description 下拉列表详情接口dto
 * @Author zhangbinglin
 * @Date 2020/6/17 16:40
 **/
@Data
public class SelectListDto {

    @ApiModelProperty("应用")
    private List<Map<String, Object>> appList;

    @ApiModelProperty("客户端")
    private List<Map<String, Object>> clientList;

    @ApiModelProperty("应用客户端")
    private List<Map<String, Object>> appClientList;

    @ApiModelProperty("应用客户端（活跃用户报表tab页使用）")
    private List<Map<String, Object>> appClientTabList;

    @ApiModelProperty("key (app id) + '_' + (client id)")
    private List<Map<String, String>> appWithDeviceList;

    @ApiModelProperty("平台")
    private List<Map<String, Object>> platformList;

    @ApiModelProperty("广告位置")
    private List<Map<String, Object>> bannerPositionList;


}
