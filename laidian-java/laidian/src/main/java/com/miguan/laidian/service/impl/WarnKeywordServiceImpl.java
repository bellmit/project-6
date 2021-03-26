package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.util.sensitive.SensitiveWordUtil;
import com.miguan.laidian.mapper.WarnKeywordMapper;
import com.miguan.laidian.service.WarnKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @Author shixh
 * @Date 2019/9/23
 **/
@Slf4j
@Service
public class WarnKeywordServiceImpl implements WarnKeywordService {

    @Autowired
    private WarnKeywordMapper warnKeywordMapper;

    @Override
    public void initWarnKeyword() {
        Set<String> fonts = warnKeywordMapper.findAllWarnKey();
        if(fonts!=null &&!fonts.isEmpty()){
            SensitiveWordUtil.initSensitiveWordMap(fonts);
        }
    }
}
