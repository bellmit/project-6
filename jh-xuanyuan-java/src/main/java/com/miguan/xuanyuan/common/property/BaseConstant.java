package com.miguan.xuanyuan.common.property;

public class BaseConstant {

    //操作系统
    public static final int ANDROID = 1;
    public static final int IOS = 2;

    //状态
    public static final int BASE_STATUS_OPEN = 1; //启用
    public static final int BASE_STATUS_CLOSE = 0; //禁用

    //状态(有待审核)
    public static final int COMPLEX_STATUS_OPEN = 0; //已启用
    public static final int COMPLEX_STATUS_CHECK = 1; //待审核
    public static final int COMPLEX_STATUS_CLOSE = 2; //已禁用
}
