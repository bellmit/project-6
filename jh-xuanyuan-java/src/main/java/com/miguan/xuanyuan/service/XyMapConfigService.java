package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyMapConfig;
import com.miguan.xuanyuan.vo.XyMapConfigVo;

import java.util.List;
/**
 * @Author kangkunhuang
 * @Description 参数配置
 * @Date 2021/1/21
 **/
public interface XyMapConfigService {
    void save(XyMapConfig xyMapConfig) throws ValidateException;
    PageInfo<XyMapConfigVo> pageList(String keyword, Integer status, String sort, Integer pageNum, Integer pageSize);

    List<XyMapConfig> findOpenConfig();
    XyMapConfig findById(Long id);
}
