package com.miguan.ballvideo.common.enums;

/** AB实验平台AppId*/
public enum AppId {
    xysp1(1,"com.mg.xyvideo"),
    xysp2(1,"com.xm98.grapefruit"),
    ggsp1(2,"com.mg.ggvideo"),
    ggsp2(2,"com.mg.westVideo"),
    dqsp(3,"com.mg.dqvideo"),
    mtsp(4,"com.mg.mtvideo"),
    xyjssp1(5,"com.mg.quickvideo"),
    xyjssp2(5,"com.mg.quickvideoios"),
    xld(6,"xld"),
    ggxcx(7,"com.mg.ggxcx"),
    dfwnl(8,"com.mg.dfwnl");

    AppId(Integer code, String name){
        this.name = name;
        this.code = code;
    }
    private String name;
    private Integer code;

    public static int getCode(String name) {
        for(AppId s : AppId.values()) {
            if(name.equals(s.name)) {
                return s.code;
            }
        }
        return 1;
    }

}
