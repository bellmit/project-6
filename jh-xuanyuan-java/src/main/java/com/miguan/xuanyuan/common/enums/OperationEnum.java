package com.miguan.xuanyuan.common.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum OperationEnum {
    FRONT(1,"frontPlat"),
    BACK(2,"backPlat");
    OperationEnum(){

    }
    private int type;
    private String name;

    OperationEnum(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType(){
        return this.type;
    }

    public String getName(){
        return this.name;
    }

    public static String getNameByType(Integer type){
        if(type == null){
            return null;
        }
        for (OperationEnum client: OperationEnum.values()) {
            if(client.getType() == type){
                return client.getName();
            }
        }
        return null;
    }

    public static List<String> getAllType(){
        List<String> allType = Lists.newArrayList();
        for (OperationEnum client: OperationEnum.values()) {
            allType.add(client.getName());
        }
        return allType;
    }
}
