package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyRender;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 渲染方式
 * @Date 2021/1/21
 **/
public interface XyRenderService {
    void save(XyRender xyRender) throws ValidateException;
    XyRender findById(Long id);
    List<XyRender> findList(String platKey, String adType);
}
