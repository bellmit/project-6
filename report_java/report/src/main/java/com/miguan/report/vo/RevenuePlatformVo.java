package com.miguan.report.vo;

import com.miguan.expression.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 根据平台统计营收相关
 * @Author zhangbinglin
 * @Date 2020/6/18 16:08
 **/
@Getter
@Setter
@ApiModel("根据平台统计营收相关")
public class RevenuePlatformVo {

    private String date;

    private String platForm;

    private String appName;

    private Double value;

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
    public boolean equals(Object obj) {
        if (obj instanceof RevenuePlatformVo) {
            RevenuePlatformVo rv = (RevenuePlatformVo)obj;
            return date.equals(rv.getDate()) && platForm.equals(rv.getPlatForm()) && appName.equals(rv.getAppName());
        }
        return false;
    }
}
