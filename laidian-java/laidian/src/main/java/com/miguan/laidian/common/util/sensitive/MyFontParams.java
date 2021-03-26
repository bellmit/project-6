package com.miguan.laidian.common.util.sensitive;

import lombok.Data;

/**
 * @Author shixh
 * @Date 2019/9/19
 **/
@Data
public class MyFontParams {
    String text;
    int matchType;

    MyFontParams(String text, int matchType) {
        this.text = text;
        this.matchType = matchType;
    }
}
