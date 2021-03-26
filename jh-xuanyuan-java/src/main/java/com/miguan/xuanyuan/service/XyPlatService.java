package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyPlat;
import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 广告平台
 * @Date 2021/1/21
 **/
public interface XyPlatService {
    void save(XyPlat xyPlat) throws ValidateException;
    XyPlat findById(Long id);
    List<XyPlat> findList();

    XyPlat getPlatDataByPlatKey(String platKey);

    List<XyPlat>  findByAdType(String adType);
}
