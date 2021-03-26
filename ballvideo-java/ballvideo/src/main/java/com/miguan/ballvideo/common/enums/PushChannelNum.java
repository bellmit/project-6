package com.miguan.ballvideo.common.enums;

/** 消息推送渠道*/
public enum PushChannelNum {
    YouMeng("YouMeng",500),
    HuaWei("HuaWei",100),
    VIVO("VIVO",500),
    OPPO("OPPO",500),
    XiaoMi("XiaoMi",500);

    PushChannelNum(String code, int count){
        this.code = code;
        this.count = count;
    }

    public static PushChannelNum val(String operate) {
        for(PushChannelNum s : values()) {    //values()方法返回enum实例的数组
            if(operate.equals(s.count))
                return s;
        }
        return null;
    }

    public static int val(PushChannelNum pushChannelNum) {
        return pushChannelNum.count;
    }
    private String code;
    private int count;
}
