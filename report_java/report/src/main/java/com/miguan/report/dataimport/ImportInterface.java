package com.miguan.report.dataimport;

import com.miguan.report.entity.BannerData;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author zhongli
 * @date 2020-06-20 
 *
 */
public interface ImportInterface {
    void doXlsxImport(XSSFWorkbook workbook);

    void doCsvImport(InputStreamReader reader) throws IOException;

    default String getAppName(String name) {
        if(name.contains("茜柚极速版")  || name.contains("西柚极速版") ){
            return "茜柚极速版";
        }
        if (name.contains("西柚") || name.contains("茜柚")) {
            return "茜柚视频";
        }
        if (name.contains("果果")) {
            return "果果视频";
        }
        if (name.contains("蜜桃")) {
            return "蜜桃视频";
        }
        if (name.contains("炫来电")) {
            return "炫来电";
        }
        if(name.contains("豆趣")){
            return "豆趣视频";
        }
        return name.split("_")[0];

    }


    default boolean pattern(BannerData data, String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        String str = data.getAdSpace();
        String[] rules = key.split(",");
        //每个,之间是and关系
        for (String rule : rules) {
            if (rule.startsWith("type=")) {
                rule = rule.substring("type=".length());
                if (!rule.equals(data.getAdSpaceType())) {
                    return false;
                }
            } else if (rule.startsWith("&^")) {
                rule = rule.substring(2);
                if (str.contains(rule)) {
                    return false;
                }
            } else if (rule.startsWith("&")) {
                rule = rule.substring(1);
                if (!str.contains(rule)) {
                    return false;
                }
            } else if (!pattern(str, rule)) {
                return false;
            }
        }
        return true;
    }

    default boolean pattern(String str, String key) {
        String[] rules = key.split("\\|");
        //每个|之间是or关系
        for (String rule : rules) {
            if (str.contains(rule)) {
                return true;
            }
        }
        return false;
    }
}
