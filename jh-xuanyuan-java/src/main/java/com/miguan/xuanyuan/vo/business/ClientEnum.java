package com.miguan.xuanyuan.vo.business;

public enum ClientEnum {
    ANDROID(1,"android"),
    IOS(2,"ios");
    ClientEnum(){

    }
    private int type;
    private String name;

    ClientEnum(Integer type,String name){
        this.type = type;
        this.name = name;
    }


    public int getType(){
        return this.type;
    }

    public String getName(){
        return this.name;
    }

    public static String getNameByType(int type){
        for (ClientEnum client:ClientEnum.values()) {
            if(client.getType() == type){
                return client.getName();
            }
        }
        return null;
    }
}
