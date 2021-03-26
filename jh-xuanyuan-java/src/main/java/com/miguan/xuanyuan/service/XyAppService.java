package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyApp;
import com.miguan.xuanyuan.vo.XyAppDetailVo;
import com.miguan.xuanyuan.vo.XyAppSimpleVo;
import com.miguan.xuanyuan.vo.XyAppVo;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 应用
 * @Date 2021/1/21
 **/
public interface XyAppService {
    void save(XyApp xyApp) throws Exception;
    PageInfo<XyAppVo> pageList(int plat, Long userId, String username, Integer type, String keyword, Integer clientType, Integer status, Integer pageNum, Integer pageSize);
    XyAppDetailVo findById(Long id);
    void deleteById(Long id);
    void updateStatus(Long appId, Integer status);
    List<XyAppSimpleVo> findList(int plat, Long userId);
    boolean existAppInfo(String appKey, String secretKey, String SHA);

    XyApp findByAppKeyAndSecret(String appKey, String secretKey);
}
