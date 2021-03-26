package com.miguan.bigdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceInfoDto {

    private List<String> distinctIds;
    private String maxDate;
}
