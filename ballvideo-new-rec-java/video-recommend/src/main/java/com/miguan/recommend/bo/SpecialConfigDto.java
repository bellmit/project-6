package com.miguan.recommend.bo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class SpecialConfigDto {

    private String specialName;
    private boolean sort1;
    private boolean sort2;
    private boolean sort3;
    private boolean sort4;
    private boolean sort5;

    public SpecialConfigDto(String specialName, JSONObject sortConfig) {
        this.specialName = specialName;

        if (sortConfig == null || sortConfig.isEmpty()) {
            this.sort1 = true;
            this.sort2 = false;
            this.sort3 = true;
            this.sort4 = false;
            this.sort5 = true;
        } else {
            this.sort1 = sortConfig.getBooleanValue("sort1");
            this.sort2 = sortConfig.getBooleanValue("sort2");
            this.sort3 = sortConfig.getBooleanValue("sort3");
            this.sort4 = sortConfig.getBooleanValue("sort4");
            this.sort5 = sortConfig.getBooleanValue("sort5");
        }
    }
}
