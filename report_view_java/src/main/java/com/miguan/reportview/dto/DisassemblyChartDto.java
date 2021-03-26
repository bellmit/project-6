package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zhongli
 * @date 2020-06-18
 * 返回前端的报文格式如下：
 * [
 *     {
 *         "dates": "2020-06-17",
 *         "type": "汇总",
 *         "value": "273481.70000000007"
 *     },
 *     {
 *         "dates": "2020-06-18",
 *         "type": "汇总",
 *         "value": "295584.69"
 *     },
 *     {
 *         "dates": "2020-06-17",
 *         "type": "果果视频Android",
 *         "value": "129485.92"
 *     },
 *     {
 *         "dates": "2020-06-18",
 *         "type": "果果视频Android",
 *         "value": "142223.15"
 *     },
 *     {
 *         "dates": "2020-06-17",
 *         "type": "茜柚视频Android",
 *         "value": "143995.78"
 *     },
 *     {
 *         "dates": "2020-06-18",
 *         "type": "茜柚视频Android",
 *         "value": "153361.54"
 *     }
 * ]
 */
@ApiModel("拆线图返回数据报文格式")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DisassemblyChartDto implements Serializable {
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("名称")
    private String type;
    @ApiModelProperty("数值")
    private double value;
    @ApiModelProperty("格式")
    private String formart;


}
