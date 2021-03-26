package com.miguan.report.common.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Getter
public enum AppVideoPackageEnum {

    XI_YOU_IOS("com.xm98.grapefruit", AppEnum.XI_YOU, ClientEnum.IOS),
    XI_YOU_ANDROID("com.mg.xyvideo", AppEnum.XI_YOU, ClientEnum.ANDROID),
    GUO_GUO_IOS("com.mg.westVideo", AppEnum.GUO_GUO, ClientEnum.IOS),
    GUO_GUO_ANDROID("com.mg.ggvideo", AppEnum.GUO_GUO, ClientEnum.ANDROID),
    DOU_QU_ANDROID("com.mg.dqvideo", AppEnum.DOU_QU, ClientEnum.ANDROID),
    XI_YOU_SPEED_ANDROID("com.mg.quickvideo", AppEnum.XI_YOU_SPEED, ClientEnum.ANDROID),
//    MI_TAO_IOS("com.mg.mtvideo", AppEnum.MI_TAO, ClientEnum.IOS),
    MI_TAO_ANDROID("com.mg.mtvideo", AppEnum.MI_TAO, ClientEnum.ANDROID);

    private String packageName;
    private AppEnum appEnum;
    private ClientEnum clientEnum;

    AppVideoPackageEnum(String packageName, AppEnum appEnum, ClientEnum clientEnum){
        this.packageName = packageName;
        this.appEnum = appEnum;
        this.clientEnum = clientEnum;
    }

    public static AppVideoPackageEnum getByAppEnumAndClientId(Integer appId, Integer clientId){
        AppVideoPackageEnum[] appVideoPackageEnums = AppVideoPackageEnum.values();
        for (AppVideoPackageEnum e: appVideoPackageEnums ) {
            if(e.getAppEnum() == AppEnum.getById(appId) && e.getClientEnum() == ClientEnum.getById(clientId)){
                return e;
            }
        }
        return null;
    }

    public static AppVideoPackageEnum getByPackageNameAndClientId(String packageName, Integer clientId){
        AppVideoPackageEnum[] appVideoPackageEnums = AppVideoPackageEnum.values();
        for (AppVideoPackageEnum e: appVideoPackageEnums ) {
            if(StringUtils.equals(e.getPackageName(), packageName) && e.getClientEnum() == ClientEnum.getById(clientId)){
                return e;
            }
        }
        return null;
    }

    public static AppVideoPackageEnum getByPackageNameAndMobileType4Adv(String packageName, Integer mobileType){
        AppVideoPackageEnum[] appVideoPackageEnums = AppVideoPackageEnum.values();
        for (AppVideoPackageEnum e: appVideoPackageEnums ) {
            if(StringUtils.equals(e.getPackageName(), packageName) && e.getClientEnum() == ClientEnum.getByAdvId(mobileType)){
                return e;
            }
        }
        return null;
    }

    public static List<String> getAllVideoPackageList(){
        AppVideoPackageEnum[] appVideoPackageEnums = AppVideoPackageEnum.values();
        List<String> allPackageName = new ArrayList<String>();
        for (AppVideoPackageEnum e: appVideoPackageEnums ) {
            allPackageName.add(e.getPackageName());
        }
        return allPackageName;
    }

    public static List<String> getVideoPackageListByClient(ClientEnum clientEnum){
        AppVideoPackageEnum[] appVideoPackageEnums = AppVideoPackageEnum.values();
        List<String> clientPackageName = new ArrayList<String>();
        for (AppVideoPackageEnum e: appVideoPackageEnums ) {
            if(e.getClientEnum() == clientEnum){
                clientPackageName.add(e.getPackageName());
            }
        }
        return clientPackageName;
    }
}
