package com.miguan.ballvideo.common.util;

import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.exception.ValidateException;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidatorUtil {

    /**
     * 正则表达式：验证URL
     */
//    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式：验证正整数(8位正整数,2位小数)
     */
    public static final String REGEX_PRICE_NUM = "^[0-9]{1,8}([.][0-9]{1,2})?$";
    public static final String REGEX_URL = "^((https|http|ftp|rtsp|mms)?:(/*))"  //https、http、ftp、rtsp、mms
            + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
            + "|" // 允许IP和DOMAIN（域名）
            + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
            + "[a-z]{2,6})" // first level domain- .com or .museum
            + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
            + "((/?)|" // a slash isn't required if there is no file name
            + "([\\s\\S]*)/?)$";
//            + "((/*)[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    public static void checkRequest(Object o,String name) throws ValidateException{
        if(o == null){
            throw new ValidateException("请输入"+name+"!");
        }
        String str = o.toString();
        if(StringUtils.isEmpty(str)){
            throw new ValidateException("请输入"+name+"!");
        }
    }

    public static void checkRequestCollection(Collection collection, String name) throws ValidateException{
        if(CollectionUtils.isEmpty(collection)){
            throw new ValidateException("请输入"+name+"!");
        }
    }

    public static void checkUrl(String url,String name) throws ValidateException{
        if(StringUtils.isEmpty(url)){
            return;
        }
        if(!isUrl(url.toLowerCase())){
            throw new ValidateException(name+"链接不规范");
        }
    }

    public static void checkRequestUrl(String url,String name) throws ValidateException{
        if(StringUtils.isEmpty(url)){
            throw new ValidateException("请输入"+name+"!");
        }
        if(!isUrl(url.toLowerCase())){
            throw new ValidateException(name+"链接不规范");
        }
    }


    /**
     * 校验字符是否必须。长度是否满足要求
     * @param fieldVal
     * @param minLeng
     * @param maxLeng
     * @param name
     * @return
     */
    public static void checkRequestStr(String fieldVal,String name,Integer minLeng,Integer maxLeng) throws ValidateException{
        if(StringUtils.isEmpty(fieldVal)){
            throw new ValidateException("请输入"+name+"!");
        }
        if(minLeng != null && fieldVal.length() < minLeng){
            throw new ValidateException(name+"的长度不能小于"+minLeng+"个字符");
        }
        if(maxLeng != null && fieldVal.length() > maxLeng){
            throw new ValidateException(name+"的长度不能超过"+maxLeng+"个字符");
        }
    }
    /**
     * 校验字符是否必须
     * @param fieldVal
     * @param name
     * @return
     */
    public static void checkRequestStr(String fieldVal,String name) throws ValidateException {
        if(StringUtils.isEmpty(fieldVal)){
            throw new ValidateException("请输入"+name+"!");
        }
    }

    /**
     * 校验字符是否必须。长度是否满足要求
     * @param fieldVal
     * @param name
     * @return
     */
    public static void checkRequestMaxStr(String fieldVal,String name,Integer maxLeng) throws ValidateException {
        if(StringUtils.isEmpty(fieldVal)){
            throw new ValidateException("请输入"+name+"!");
        }

        if(maxLeng != null && fieldVal.length() > maxLeng){
            throw new ValidateException(name+"的长度不能超过"+maxLeng+"个字符");
        }
    }


    /**
     * 长度是否满足要求
     * @param fieldVal
     * @param name
     * @return
     */
    public static void checkMaxStr(String fieldVal,String name,Integer maxLeng) throws ValidateException {
        if(StringUtils.isEmpty(fieldVal)){
            return;
        }

        if(maxLeng != null && fieldVal.length() > maxLeng){
            throw new ValidateException(name+"的长度不能超过"+maxLeng+"个字符");
        }
    }

    /**
     * 校验字符是否必须，并且长度是否符合要求
     * @param price
     * @param name
     * @return
     */
    public static void checkPriceNum(BigDecimal price, String name) throws ValidateException {
        if(price == null){
            return ;
        }
        boolean match = Pattern.matches(REGEX_PRICE_NUM, price.toString());
        if(!match){
            throw new ValidateException(name+"的正整数不能超过8个,并且仅允许带有两位小数点！");
        }
    }

    /**
     * 校验字符是否必须，并且长度是否符合要求
     * @param price
     * @param name
     * @return
     */
    public static void checkRequestPriceNum(BigDecimal price, String name) throws ValidateException {
        if(price == null){
            throw new ValidateException("请输入"+name+"!");
        }
        boolean match = Pattern.matches(REGEX_PRICE_NUM, price.toString());
        if(!match){
            throw new ValidateException(name+"的正整数不能超过8个,并且仅允许带有两位小数点！");
        }
    }

    /**
     * 检查特定类型
     * @param fieldName
     * @param name
     * @return
     */
    public static void checkType(Map<Integer,String> typeMap, Integer fieldName, String name) throws ValidateException {
        if(fieldName == null || MapUtils.isEmpty(typeMap)){
            return;
        }

        if(StringUtils.isEmpty(typeMap.get(fieldName))){
            throw new ValidateException(name+"参数有误！");
        }
    }

    /**
     * 检查特定类型
     * @param fieldName
     * @param name
     * @return
     */
    public static void checkRequestType(Map<Integer,String> typeMap, Integer fieldName, String name) throws ValidateException {
        if(fieldName == null || MapUtils.isEmpty(typeMap)){
            throw new ValidateException("请选择"+name+"！");
        }

        if(StringUtils.isEmpty(typeMap.get(fieldName))){
            throw new ValidateException(name+"参数有误！");
        }
    }

}
