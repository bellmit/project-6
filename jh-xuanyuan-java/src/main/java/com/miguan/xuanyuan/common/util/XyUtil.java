package com.miguan.xuanyuan.common.util;

import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.dto.AdCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tool.util.StringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 公共工具类
 * @Date 2020/12/14 14:33
 **/
@Slf4j
public class XyUtil {
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


    public static boolean validateVersion(AdCodeDto adCode, String version) {
        try{
            if(StringUtils.isEmpty(version) || adCode == null){
                return true;
            }
            String operation = adCode.getVersionOperation();
            String versions = adCode.getVersions();
            //当operation=in时. operation为空。则返回false
            if(StringUtils.isNotEmpty(operation) && XyConstant.OPERA_IN.equals(operation) && StringUtils.isEmpty(versions)){
                return false;
            }

            if(StringUtils.isEmpty(operation) || StringUtils.isEmpty(versions)){
                return true;
            }
            String opera = XyConstant.VERSION_OPERATION_MAP.get(operation);
            if(StringUtils.isEmpty(opera)){
                return true;
            }
            if(XyConstant.OPERA_ALL.equals(opera)){
                return true;
            }
            if(XyConstant.OPERA_IN.equals(opera)){
                String[] verSplit = versions.split(",");
                return Lists.newArrayList(verSplit).contains(version) ? true : false;
            }
            if(XyConstant.OPERA_NOT_IN.equals(opera)){
                String[] verSplit = versions.split(",");
                return Lists.newArrayList(verSplit).contains(version) ? false : true;
            }
            if(XyConstant.OPERA_EQUAL.equals(opera)){
                String[] verSplit = versions.split(",");
                return Lists.newArrayList(verSplit).contains(version) ? true : false;
            }
            if(XyConstant.OPERA_GREATER.equals(opera)){
                String ver = versions.split(",")[0];
                return VersionUtil.compareIsHigh(version,ver);
            }
            if(XyConstant.OPERA_GREATER_EQUAL.equals(opera)){
                String ver = versions.split(",")[0];
                return VersionUtil.compare(version,ver);
            }
            if(XyConstant.OPERA_LESS.equals(opera)){
                String ver = versions.split(",")[0];
                return VersionUtil.compareIsHigh(ver,version);
            }
            if(XyConstant.OPERA_LESS_EQUAL.equals(opera)){
                String ver = versions.split(",")[0];
                return VersionUtil.compare(ver,version);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    public static boolean validateChannel(AdCodeDto adCode, String channel) {
        try{
            if(StringUtils.isEmpty(channel) || adCode == null){
                return true;
            }
            String operation = adCode.getChannelOperation();
            String channels = adCode.getChannels();
            //当operation=in时. operation为空。则返回false
            if(StringUtils.isNotEmpty(operation) && XyConstant.OPERA_IN.equals(operation) && StringUtils.isEmpty(channels)){
                return false;
            }
            if(StringUtils.isEmpty(operation) || StringUtils.isEmpty(channels)){
                return true;
            }
            String opera = XyConstant.CHANNEL_OPERATION_MAP.get(operation);
            if(StringUtils.isEmpty(opera)){
                return true;
            }
            if(XyConstant.OPERA_ALL.equals(opera)){
                return true;
            }
            if(XyConstant.OPERA_IN.equals(opera)){
                String[] chanSplit = channels.split(",");
                return Lists.newArrayList(chanSplit).contains(channel) ? true : false;
            }
            if(XyConstant.OPERA_NOT_IN.equals(opera)){
                String[] chanSplit = channels.split(",");
                return Lists.newArrayList(chanSplit).contains(channel) ? false : true;
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 填充指定字符串达到给定的长度
     *
     * @param originStr 原始字符串
     * @param length 长度，当原始字符串长度达不到给定长度，则向左边填充数据
     * @param padStr 填充字符串
     * @return
     */
    public static String strPad(String originStr, int length, String padStr) {
        StringBuilder str = new StringBuilder();

        int strLen = originStr.length();
        if (strLen < length) {
            for (int i = 0; i < (length - strLen); i++) {
                str.append(padStr);
            }
            str.append(originStr);
        }
        return str.toString();
    }
}
