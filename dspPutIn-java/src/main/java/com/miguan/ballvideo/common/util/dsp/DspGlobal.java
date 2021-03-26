package com.miguan.ballvideo.common.util.dsp;

import cn.jiguang.common.utils.StringUtils;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertConfigVo;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertEcpmVo;
import tool.util.NumberUtil;
import tool.util.StringUtil;
import java.util.List;
import java.util.Map;

/**
 * DSP平台相关内存存储工具
 * @Author suhj
 * @Date 2020/9/10
 **/
public class DspGlobal {

    //存储广告配置
    public static volatile Map<String, String> CONF_MAP = Maps.newHashMap();

    //存储98自投所有计划ID+创意ID+代码位的ecpm   key:计划ID+_+创意ID+_+代码位  value:ecpm
    public static volatile Map<String, Double> ALL_98DU_ECPM_MAP = Maps.newHashMap();

    //存储98自投所有计划ID+创意ID+代码位的有效曝光数   key:计划ID+_+创意ID+_+代码位  value:有效曝光数
    public static volatile Map<String, Integer> ALL_98DU_SHOW_MAP = Maps.newHashMap();

    //存储98自投所有计划ID+创意ID+代码位的有效点击数   key:计划ID+_+创意ID+_+代码位  value:有效点击数
    public static volatile Map<String, Integer> ALL_98DU_CLICK_MAP = Maps.newHashMap();

    //存储所有代码位的ecpm   key:代码位  value:ecpm
    public static volatile Map<String, Double> AVG_CODE_ECPM_MAP = Maps.newHashMap();

    public static int getInt(String key) {
        return NumberUtil.getInt(StringUtil.isNull(CONF_MAP.get(key)));
    }

    public static double getDouble(String key) {
        return NumberUtil.getDouble(StringUtil.isNull(CONF_MAP.get(key)));
    }

    public static String getValue(String key) {
        return StringUtil.isNull(CONF_MAP.get(key));
    }

    public static Object getObject(String key) {
        return CONF_MAP.get(key);
    }

    public static Map<String, Double> getAll98duEcpmMap() {
        return ALL_98DU_ECPM_MAP;
    }

    public static Map<String, Integer> getAll98duShowMap() {
        return ALL_98DU_SHOW_MAP;
    }

    public static Map<String, Integer> getAll98duClickMap() {
        return ALL_98DU_CLICK_MAP;
    }

    public static Map<String, Double> getAvgCodeEcpmMap() {
        return AVG_CODE_ECPM_MAP;
    }

    public static void putConfigAll(List<IdeaAdvertConfigVo> conLst) {
        //清理配置缓存
        CONF_MAP.clear();
        conLst.forEach(con -> CONF_MAP.put(con.getCode(),con.getValue()));
    }


    public static void putEcpmAll(List<IdeaAdvertEcpmVo> ecpmLst) {
        ALL_98DU_ECPM_MAP.clear();
        ALL_98DU_SHOW_MAP.clear();
        ALL_98DU_CLICK_MAP.clear();
        AVG_CODE_ECPM_MAP.clear();
        Map<String,String> incomeExpMap = Maps.newHashMap();
        ecpmLst.forEach(ecpm -> {
            if(StringUtils.isNotEmpty(ecpm.getPlanId()) && !"0".equals(ecpm.getPlanId())
                    && StringUtils.isNotEmpty(ecpm.getDesignId()) && !"0".equals(ecpm.getDesignId())){
                String key = ecpm.getPlanId() + "_" + ecpm.getDesignId() + "_" + ecpm.getCode();
                ALL_98DU_ECPM_MAP.put(key, ecpm.getEcpm());
                ALL_98DU_SHOW_MAP.put(key, (int)ecpm.getExposure());
                ALL_98DU_CLICK_MAP.put(key, (int)ecpm.getClick());
                if(incomeExpMap.isEmpty() || StringUtils.isEmpty(incomeExpMap.get(ecpm.getCode()))){
                    incomeExpMap.put(ecpm.getCode(),ecpm.getIncome() + "_" + ecpm.getExposure());
                }else {
                    String incomeExp = incomeExpMap.get(ecpm.getCode());
                    double income = Double.parseDouble(incomeExp.split("_")[0]);
                    double exposure = Double.parseDouble(incomeExp.split("_")[1]);
                    income += ecpm.getIncome();
                    exposure += ecpm.getExposure();
                    incomeExpMap.put(ecpm.getCode(),income + "_" + exposure);
                }
            }
        });
        ecpmLst.forEach(ecpm -> {
            if(!incomeExpMap.isEmpty() && StringUtils.isNotEmpty(incomeExpMap.get(ecpm.getCode()))){
                String incomeExp = incomeExpMap.get(ecpm.getCode());
                double income = Double.parseDouble(incomeExp.split("_")[0]);
                double exposure = Double.parseDouble(incomeExp.split("_")[1]);
                double avgEcpm = exposure != 0 ?
                        (double) (Math.round(income/exposure*1000*100)/100.0) : 0;
                AVG_CODE_ECPM_MAP.put(ecpm.getCode(),avgEcpm);
            }else {
                AVG_CODE_ECPM_MAP.put(ecpm.getCode(),ecpm.getEcpm());
            }
        });
    }
}
