package com.miguan.report.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 广点通App应用ID
 */
@Getter
public enum GdtAppKeyEnum {

    XI_YOU_VIDEO_ANDROID("茜柚Android", "1109787675", AppEnum.XI_YOU, ClientEnum.ANDROID),
    GUO_GUO_VIDEO_ANDROID("果果Android", "1110118798", AppEnum.GUO_GUO, ClientEnum.ANDROID),
    XUAN_LAI_DIAN_ANDROID("炫来电Android", "1109644571", AppEnum.LAI_DIAN, ClientEnum.ANDROID),
    MI_TAO_VIDEO_ANDROID("蜜桃Android", "1110143343", AppEnum.MI_TAO, ClientEnum.ANDROID),
    XI_YOU_VIDEO_IOS("茜柚Ios", "1110490327", AppEnum.XI_YOU, ClientEnum.IOS),
    GUO_GUO_VIDEO_IOS("果果Ios", "1109869225", AppEnum.GUO_GUO, ClientEnum.IOS),
    XUAN_LAI_DIAN_IOS("炫来电Ios", "1109647473", AppEnum.LAI_DIAN, ClientEnum.IOS);

    private String name;
    private String appId;
    private AppEnum appEnum;
    private ClientEnum clientEnum;

    GdtAppKeyEnum(String name, String appId, AppEnum appEnum, ClientEnum clientEnum) {
        this.name = name;
        this.appId = appId;
        this.appEnum = appEnum;
        this.clientEnum = clientEnum;
    }

    public static GdtAppKeyEnum getByAppId(String appId) {
        GdtAppKeyEnum[] enums = GdtAppKeyEnum.values();
        for (GdtAppKeyEnum e : enums) {
            if (StringUtils.equals(e.getAppId(), appId)) {
                return e;
            }
        }
        return null;
    }
}
