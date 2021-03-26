package com.miguan.xuanyuan.dto.ab;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Condition {


    /**
     * 渠道操作类型集合
     *
     */
    public static final List<String> CHANNEL_OPERATION_SET = Arrays.asList("in", "not in");

    /**
     * 版本操作类型集合
     *
     */
    public static final List<String> VERSION_OPERATION_SET = Arrays.asList("in", "not in", "=", ">", ">=", "<", "<=");


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
