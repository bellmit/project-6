package com.miguan.report.dto;

import com.miguan.report.vo.RevenuePlatformVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @Description 自定义列类
 * @Author zhangbinglin
 * @Date 2020/6/22 10:55
 **/
@Data
public class BannerQuotaDto {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("字段名")
    private String name;

    @ApiModelProperty("字段")
    private String field;

    @ApiModelProperty("字段是否显示, true：显示，false：不显示")
    private boolean select;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BannerQuotaDto) {
            BannerQuotaDto bqd = (BannerQuotaDto)obj;
            return field.equals(bqd.field);
        }
        return false;
    }
}
