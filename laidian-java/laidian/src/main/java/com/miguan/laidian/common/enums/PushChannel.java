package com.miguan.laidian.common.enums;

/** 消息推送渠道*/
public enum PushChannel {
    YouMeng("YouMeng","友盟"),
    HuaWei("HuaWei","华为"),
    VIVO("VIVO","VIVO"),
    OPPO("OPPO","OPPO"),
    XiaoMi("XiaoMi","小米");

    private String name;
    private String code;

    PushChannel(String code, String name){
        this.name = name;
        this.code = code;
    }

    public static PushChannel val(String operate) {
        for(PushChannel s : values()) {    //values()方法返回enum实例的数组
            if(operate.equals(s.code))
                return s;
        }
        return null;
    }


}
