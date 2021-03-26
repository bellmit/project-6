package com.miguan.idmapping.common.enums;

/**
 * @author zhongli
 * @date 2020-07-21 
 *
 */
public enum UserFromEnums {
    /**
     *
     */
    ANDROID("android"),
    IOS("ios"),
    H5("h5"),
    WX("weixin"),
    XCX("xiaochengxu"),
    WEB("web");

    private final String name;

    UserFromEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserFromEnums toEnums(String value) {
        for (UserFromEnums typeEnum : UserFromEnums.values()) {
            if (typeEnum.getName().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
