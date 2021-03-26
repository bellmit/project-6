package com.miguan.ballvideo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @Author shixh
 * @Date 2019/12/1
 **/
@Slf4j
public class VersionUtil {

    /**
     * 判断2位数的版本，比如2.3
     * @param appVersion
     * @param v
     * @return
     */
    public static boolean isHigh(String appVersion, double v) {
        if(StringUtils.isEmpty(appVersion) || "null".equals(appVersion))return false;//没传版本号默认返回false；
        int appVersionNum = Integer.parseInt(appVersion.replace(".",""));
        BigDecimal bg = new BigDecimal(v * 100);
        double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return appVersionNum>(int)doubleValue;
    }

  /**
   * 判断3位数的版本，比如2.3.1
   * @param appVersion
   * @param v
   * @return
   */
  public static boolean isHigh(String appVersion, String v) {
        if(StringUtils.isEmpty(appVersion))return false;//没传版本号默认返回false；
        int appVersionNum = Integer.parseInt(appVersion.replace(".",""));
        int v2 = Integer.parseInt(v.replace(".",""));
        return appVersionNum>v2;
    }

    /**
     * 版本号为空处理
     * 1 生产环境存在appVersion="未知版本"问题，添加条件过滤；
     * @param appVersion
     * @return
     */
    public static String getVersion(String appVersion) {
        if (StringUtils.isBlank(appVersion) || "未知版本".equals(appVersion)) {
            return "1.7.0";
        } else {
            return appVersion;
        }
    }

    /**
     *  比较版本区间 versionStart<=version<=versionEnd 返回true 否则返回false
     * @param versionStart 开始版本
     * @param versionEnd 结束版本
     * @param version app前端传入版本号
     * @return
     */
    public static boolean isBetween(String versionStart,String versionEnd,String version) {
        if(StringUtils.isEmpty(versionStart)
                || StringUtils.isEmpty(versionEnd)
                || StringUtils.isEmpty(version))return false;//没传版本号默认返回false；
        boolean result = compare(version, versionStart);
        if (result) {
            result = compare(versionEnd, version);
        }
        return result;
    }

    /**
     * 版本比较 version1>version2 返回true 否则返回false
     * @param version1 app前端传入版本号
     * @param version2 待比较的版本号
     * @return
     */
    public static boolean compareIsHigh(String version1,String version2){
        String [] xx=version1.split("\\.");
        String [] yy=version2.split("\\.");
        boolean a=false;
        try {
            for(int x=0,y=0;x<xx.length||y<yy.length;x++,y++){
                int left=(x<xx.length)?Integer.parseInt(xx[x]):0;
                int right=(y<yy.length)?Integer.parseInt(yy[y]):0;
                if(left>right){
                    return true;
                }else if(left==right){
                    continue;
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("版本比较出现错误，请检查！");
        }
        return a;
    }

    /**
     * 版本比较 version1>=version2 返回true 否则返回false
     * @param version1 app前端传入版本号
     * @param version2 待比较的版本号
     * @return
     */
    public static boolean compare(String version1,String version2){
        String [] xx=version1.split("\\.");
        String [] yy=version2.split("\\.");
        boolean a=true;
        try {
            for(int x=0,y=0;x<xx.length||y<yy.length;x++,y++){
                int left=(x<xx.length)?Integer.parseInt(xx[x]):0;
                int right=(y<yy.length)?Integer.parseInt(yy[y]):0;
                if(left>right){
                    return true;
                }else if(left==right){
                    continue;
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("版本比较出现错误，请检查！");
        }
        return a;
    }
}
