package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

@Data
public class Condition {
    private String key;
    private String operation;
    private String value;
    private String connector;

    public Condition() {
    }

    public Condition(String key, String operation, String value){
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public Condition(String connector) {
        this.connector = connector;
    }
}
