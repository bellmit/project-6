package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

@Data
public class Variable {
    private boolean inEdit = false;
    private boolean mouseenter = false;
    private String param_type_display = "Number";
    private String param_desc = "暂无使用";
    private String param_value;
    private Integer param_type;
    private String param_key;

    public Variable() {
    }

    public Variable(String param_value, Integer param_type, String param_key) {
        this.param_value = param_value;
        this.param_type = param_type;
        this.param_key = param_key;
    }
}
