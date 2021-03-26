package com.miguan.flow.common.util;

import lombok.extern.slf4j.Slf4j;
import tool.util.StringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 公共工具类
 * @Date 2020/12/14 14:33
 **/
@Slf4j
public class FlowUtil {
    /**
     * 实体类对象转换成Map
     * @param obj
     * @return
     */
    public static Map<String, Object> convertObjToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Field f = obj.getClass().getDeclaredField(
                            fields[i].getName());
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    if(o != null) {
                        reMap.put(fields[i].getName(), o);
                    }
                } catch (Exception e) {
                    log.error("实体类对象转换成Map异常", e);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return reMap;
    }

    /**
     * 截取字符串末尾的数字
     * @param str
     * @return
     */
    public static String removeEndNum(String str) {
        if(StringUtil.isBlank(str)) {
            return str;
        }
        String[] array = str.split("[\\D]+");
        if(array == null || array.length == 0) {
            return str;
        }
        return str.substring(0, str.lastIndexOf(array[array.length-1]));
    }
}
