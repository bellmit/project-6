package com.miguan.xuanyuan.common.enums;


/**
 * 权限类型
 *
 */
public enum PermissionTypeEnum {

    DIRECTORY(1, "目录"),
    MENU(2, "菜单"),
    FUNC(3, "功能");



    PermissionTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }


    private int type;
    private String name;


    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static boolean exist(int type) {
        for (PermissionTypeEnum p : PermissionTypeEnum.values()) {
            if (p.getType() == type) {
                return true;
            }
        }
        return false;
    }

}