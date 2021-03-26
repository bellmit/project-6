package com.miguan.report.common.enums;

import lombok.Getter;


/**
 * 友盟App应用ID
 */
@Getter
public enum UmengAppKeyEnum {

    XI_YOU_VIDEO_ANDROID("茜柚Android", "5d59ff453fc195a47d000c8c", AppEnum.XI_YOU, ClientEnum.ANDROID, "com.mg.xyvideo"),
    GUO_GUO_VIDEO_ANDROID("果果Android", "5dd52df2570df3a75800008d", AppEnum.GUO_GUO, ClientEnum.ANDROID, "com.mg.ggvideo"),
    MI_TAO_VIDEO_ANDROID("蜜桃Android", "5dd52dc0570df38449000228", AppEnum.MI_TAO, ClientEnum.ANDROID, "com.mg.mtvideo"),
    DOU_QU_VIDEO_ANDROID("豆趣Android", "5f51f7d17823567fd863fd8f", AppEnum.DOU_QU, ClientEnum.ANDROID, "com.mg.dqvideo"),
    XI_YOU_SPEED_VIDEO_ANDROID("茜柚极速版Android", "5f30a24dd309322154764419", AppEnum.XI_YOU_SPEED, ClientEnum.ANDROID, "com.mg.quickvideo"),
    XUAN_LAI_DIAN_ANDROID("炫来电Android", "5d4a79984ca357271d00086d", AppEnum.LAI_DIAN, ClientEnum.ANDROID, "com.mg.phonecall"),
    XI_YOU_VIDEO_IOS("茜柚Ios", "5e958fae570df37c1f000042", AppEnum.XI_YOU, ClientEnum.IOS, "com.xm98.grapefruit"),
    GUO_GUO_VIDEO_IOS("果果Ios", "5d4bf0d7570df313100001ca", AppEnum.GUO_GUO, ClientEnum.IOS, "com.mg.westVideo"),
    QI_LI("飞快清理大师","5ff6885eadb42d5826a18ad0",AppEnum.QI_LI,ClientEnum.ANDROID,"com.ned.fastcleaner"),
   // WAN_NLI("锦鲤万年历","5fffae4bf1eb4f3f9b5df1e3",AppEnum.WAN_NLI,ClientEnum.IOS,"com.98du.jinlicalender");
    HAOLU_VIDEO("好鹿视频","602f6adc425ec25f10f7f99b",AppEnum.HAO_LU,ClientEnum.ANDROID,"com.zl.hlvideo");
    private String name;
    private String appId;
    private AppEnum appEnum;  //对应app表的id
    private ClientEnum clientEnum;  //客户端类型, 1:安卓，2：ios
    private String packageName;  //包名

    UmengAppKeyEnum(String name, String appId, AppEnum appEnum, ClientEnum clientEnum, String packageName) {
        this.name = name;
        this.appId = appId;
        this.appEnum = appEnum;
        this.clientEnum = clientEnum;
        this.packageName = packageName;
    }
}
