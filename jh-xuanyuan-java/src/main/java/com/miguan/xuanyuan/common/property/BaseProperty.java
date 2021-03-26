package com.miguan.xuanyuan.common.property;

import com.google.common.collect.Lists;

import java.util.List;

public class BaseProperty {
    //操作系统
    public static final List<Integer> ClientType = Lists.newArrayList();
    //基础状态
    public static final List<Integer> baseStatus = Lists.newArrayList();
    //有待审核的状态
    public static final List<Integer> complexStatus = Lists.newArrayList();

    static {
        //操作系统
        ClientType.add(BaseConstant.ANDROID);
        ClientType.add(BaseConstant.IOS);

        //基础状态
        baseStatus.add(BaseConstant.BASE_STATUS_OPEN);
        baseStatus.add(BaseConstant.BASE_STATUS_CLOSE);

        //有待审核的状态
        complexStatus.add(BaseConstant.COMPLEX_STATUS_OPEN);
        complexStatus.add(BaseConstant.COMPLEX_STATUS_CHECK);
        complexStatus.add(BaseConstant.COMPLEX_STATUS_CLOSE);
    }

}
