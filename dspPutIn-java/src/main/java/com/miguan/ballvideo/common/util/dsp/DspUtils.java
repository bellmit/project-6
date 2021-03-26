package com.miguan.ballvideo.common.util.dsp;

import com.miguan.ballvideo.common.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: dspPutIn-java
 * @description: Dsp工具类
 * @author: suhj
 * @create: 2020-09-23 09:20
 **/
public class DspUtils {

    /**
     * str转化成sql  例如 1,2,3 -> '1','2','3'
     * @param str 以逗号隔开
     * @return
     */
    public static String sqlStrChg(String str){
        List<String> strLst = Arrays.asList(str.split(","))
                .stream().filter(s -> !StringUtil.isEmpty(s)).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(strLst)){
            return null;
        }
        StringBuffer strBuf = new StringBuffer("'");
        strLst.stream().forEach(st -> strBuf.append(st).append("','"));
        return strBuf.substring(0,strBuf.length()-2);
    }


}
