package com.miguan.laidian.common.constants;

public enum VersionEnum {

    VERSION_ENUM_TYPE_OPEN("20"),//是否强制更新：10--否，20--是
    VERSION_ENUM_TYPE_OFF("10");//是否强制更新：10--否，20--是

    private String code;

    private VersionEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
