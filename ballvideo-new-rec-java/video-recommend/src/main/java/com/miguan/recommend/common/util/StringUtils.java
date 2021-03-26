package com.miguan.recommend.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 移除反斜杠字符
     * @param string
     * @return
     */
    public static String removeBackslashCharacter(String string) {
        String reg = "\\\\";
        Pattern pattern = Pattern.compile(reg);
        Matcher mc=pattern.matcher(string);
        return mc.replaceAll("");
    }
}
