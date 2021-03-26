package com.miguan.report.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 快手App应用ID
 */
@Getter
public enum KuaiShouAppKeyEnum {

    XI_YOU_VIDEO_ANDROID("茜柚Android","519800001", AppEnum.XI_YOU, ClientEnum.ANDROID),
    GUO_GUO_VIDEO_IOS("果果Ios","519800002", AppEnum.GUO_GUO, ClientEnum.IOS),
    GUO_GUO_VIDEO_ANDROID("果果Android","519800003", AppEnum.GUO_GUO, ClientEnum.ANDROID),
    XUAN_LAI_DIAN_ANDROID("炫来电Android","519800004", AppEnum.LAI_DIAN, ClientEnum.ANDROID),
    XI_YOU_VIDEO_IOS("茜柚Ios","519800005", AppEnum.XI_YOU, ClientEnum.IOS);

    private String name;
    private String appId;
    private AppEnum appEnum;
    private ClientEnum clientEnum;

    KuaiShouAppKeyEnum(String name, String appId, AppEnum appEnum, ClientEnum clientEnum){
        this.name = name;
        this.appId = appId;
        this.appEnum = appEnum;
        this.clientEnum = clientEnum;
    }

    public static KuaiShouAppKeyEnum getByAppId(String appId){
        KuaiShouAppKeyEnum[] enums = KuaiShouAppKeyEnum.values();
        for (KuaiShouAppKeyEnum e : enums){
            if(StringUtils.equals(e.getAppId(), appId)){
                return e;
            }
        }
        return null;
    }
}
