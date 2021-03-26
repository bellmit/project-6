package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 选项配置
 * @Date 2021/1/21
 **/
public interface XyOptionItemConfigService {
    void saveBatch(String configCode, List<XyOptionItemConfig> xyOptionItemConfigs)  throws ValidateException;
    List<XyOptionItemConfig> findByConfigCode(String configCode);
    XyOptionItemConfig findById(Long id);
    XyOptionItemConfig findByCodeAndKey(String codeConfig, String itemKey);

    List<XyOptionItemConfig> findOperationPlatType(List<String> allConfigCode, String configCode);
}
