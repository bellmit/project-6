package com.miguan.report.dto;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description 数据明细列表DTO
 * @Author zhangbinglin
 * @Date 2020/6/22 11:29
 **/
@Data
public class DataDetailDto {

    @ApiModelProperty("表头列表")
    private List<BannerQuotaDto> headerList;
    
    @ApiModelProperty("数据列表")
    private PageInfo<LinkedHashMap<String, Object>> dataList;
}
