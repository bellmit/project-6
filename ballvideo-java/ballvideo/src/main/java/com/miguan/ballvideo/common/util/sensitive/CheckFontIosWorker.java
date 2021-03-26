package com.miguan.ballvideo.common.util.sensitive;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author daoyu
 * @Date 2020/7/03
 **/
@Slf4j
public class CheckFontIosWorker extends Worker {

    @Override
    public Object handle(Object input) {
        MyFontParams params =(MyFontParams)input;
        for (int i = 0; i < params.getText().length(); i++) {
            int matchFlag = SensitiveWordUtil.checkSensitiveWordIos(params.getText(), i, params.getMatchType());
            if (matchFlag > 0) {
                return 1;
            }
        }
        return 0;
    }
}
