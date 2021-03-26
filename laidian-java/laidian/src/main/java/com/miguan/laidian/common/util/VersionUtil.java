package com.miguan.laidian.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author shixh
 * @Date 2019/12/1
 **/
@Slf4j
public class VersionUtil {
    /**
     * 版本比较 version1>version2 返回true 否则返回false
     * @param version1 app前端传入版本号
     * @param version2 待比较的版本号
     * @return
     */
    public static boolean compareIsHigh(String version1,String version2){
        if (StringUtils.isEmpty(version1)) return false;//没传版本号默认返回false；
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
}
