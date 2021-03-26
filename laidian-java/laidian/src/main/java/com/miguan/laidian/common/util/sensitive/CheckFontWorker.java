package com.miguan.laidian.common.util.sensitive;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author shixh
 * @Date 2019/9/19
 **/
@Slf4j
public class CheckFontWorker extends Worker {

    @Override
    public Object handle(Object input) {
        MyFontParams params =(MyFontParams)input;
        //log.info("处理敏感词开始：检查内容"+params.getText());
        for (int i = 0; i < params.getText().length(); i++) {
            int matchFlag = SensitiveWordUtil.checkSensitiveWord(params.getText(), i, params.getMatchType()); //判断是否包含敏感字符
            if (matchFlag > 0) {
                return 1;
            }
        }
        //log.info("当前检查未含有敏感词");
        return 0;
    }
}
