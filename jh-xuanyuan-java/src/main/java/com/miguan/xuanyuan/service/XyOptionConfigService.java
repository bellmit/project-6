package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyOptionConfig;
import com.miguan.xuanyuan.vo.XyOptionConfigVo;


/**
 * @Author kangkunhuang
 * @Description 选项配置
 * @Date 2021/1/21
 **/
public interface XyOptionConfigService {
    void save(XyOptionConfig xyOptionConfig)  throws ValidateException;
    PageInfo<XyOptionConfigVo> pageList(String keyword, Integer status, String sort, Integer pageNum, Integer pageSize);
    XyOptionConfig findById(Long id);
    /**
     * @Author kangkunhuang
     * @Description 检验是否存在这个key: true:存在, false:不存在
     * @Date 2021/1/22
     **/
    boolean judgeExistCode(String code);
}
