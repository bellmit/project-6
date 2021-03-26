package com.miguan.report.dto;

import com.miguan.expression.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Author zhangbinglin
 * @Date 2020/6/17 11:52
 **/
@Data
@ApiModel("折线图表DTO")
public class LineChartDto  implements Comparable<LineChartDto>{

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("类型（如：应用名称，或者平台名称）")
    private String type;

    @ApiModelProperty("数值")
    private Double value;

    @ApiModelProperty("排序标识")
    private String sortTag;

    public void setDate(String date) {
        if(StringUtils.isBlank(date)) {
            return;
        }
        String[] dateArray = date.split(",");
        if(dateArray.length == 1) {
            this.date = dateArray[0];
        } else {
            List<String> dateList = new ArrayList<>();
            dateList.add(dateArray[0]);
            dateList.add(dateArray[dateArray.length - 1]);
            this.date = String.join("至", dateList);
        }
    }

    @Override
    public int compareTo(LineChartDto o) {
        return (date + (sortTag == null ? "" : sortTag)) .compareTo(o.date + (o.sortTag == null ? "" : o.sortTag));
    }
}
