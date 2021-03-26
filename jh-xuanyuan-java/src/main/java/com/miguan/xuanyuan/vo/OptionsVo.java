package com.miguan.xuanyuan.vo;

import lombok.Data;

@Data
public class OptionsVo {

    private String value;

    private String label;


    public OptionsVo(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
