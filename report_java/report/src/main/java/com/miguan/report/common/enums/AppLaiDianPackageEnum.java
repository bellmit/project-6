package com.miguan.report.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum AppLaiDianPackageEnum {

    LAI_DIAN_ANDROID("com.mg.phonecall",AppEnum.LAI_DIAN, ClientEnum.ANDROID);

    private String packageName;
    private AppEnum appEnum;
    private ClientEnum clientEnum;

    AppLaiDianPackageEnum(String packageName, AppEnum appEnum, ClientEnum clientEnum){
        this.packageName = packageName;
        this.appEnum = appEnum;
        this.clientEnum = clientEnum;
    }

    public static AppLaiDianPackageEnum getByPackageNameAndClientId(String appName, Integer clientId){
        AppLaiDianPackageEnum[] appVideoPackageEnums = AppLaiDianPackageEnum.values();
        for (AppLaiDianPackageEnum e: appVideoPackageEnums ) {
            if(e.getPackageName().toLowerCase().equals(appName.toLowerCase()) && e.getClientEnum() == ClientEnum.getById(clientId)){
                return e;
            }
        }
        return null;
    }

    public static AppLaiDianPackageEnum getByAppEnumAndClientId(Integer appId, Integer clientId){
        AppLaiDianPackageEnum[] appVideoPackageEnums = AppLaiDianPackageEnum.values();
        for (AppLaiDianPackageEnum e: appVideoPackageEnums ) {
            if(e.getAppEnum() == AppEnum.getById(appId) && e.getClientEnum() == ClientEnum.getById(clientId)){
                return e;
            }
        }
        return null;
    }

    public static AppLaiDianPackageEnum getByPackageNameAndMobileType4Adv(String packageName, Integer mobileType){
        AppLaiDianPackageEnum[] appVideoPackageEnums = AppLaiDianPackageEnum.values();
        for (AppLaiDianPackageEnum e: appVideoPackageEnums ) {
            if(StringUtils.equals(e.getPackageName(), packageName) && e.getClientEnum() == ClientEnum.getByAdvId(mobileType)){
                return e;
            }
        }
        return null;
    }

    public static List<String> getAllLaiDianPackageList(){
        AppLaiDianPackageEnum[] appVideoPackageEnums = AppLaiDianPackageEnum.values();
        List<String> allPackageName = new ArrayList<String>();
        for (AppLaiDianPackageEnum e: appVideoPackageEnums ) {
            allPackageName.add(e.getPackageName());
        }
        return allPackageName;
    }

    public static List<String> getLaiDianPackageListByClient(ClientEnum clientEnum){
        AppLaiDianPackageEnum[] appVideoPackageEnums = AppLaiDianPackageEnum.values();
        List<String> clientPackageName = new ArrayList<String>();
        for (AppLaiDianPackageEnum e: appVideoPackageEnums ) {
            if(e.getClientEnum() == clientEnum){
                clientPackageName.add(e.getPackageName());
            }
        }
        return clientPackageName;
    }
}
