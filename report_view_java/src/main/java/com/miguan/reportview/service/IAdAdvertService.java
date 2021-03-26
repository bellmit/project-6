package com.miguan.reportview.service;

import com.miguan.reportview.entity.AdAdvertPosition;
import com.miguan.reportview.vo.AdSpaceVo;

import java.util.List;

/**
 * <p>
 * 广告位置表（广告相关表统一ad_开头） 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface IAdAdvertService {

    void sysAdSpace();

    List<AdSpaceVo> getAdSpaceByApp(Integer appType, String... appPackage);

    List<String> findAdCodeForLike(String likeCode);

    AdSpaceVo getAdspaceForName(String name);

    List<AdAdvertPosition> getAll();

    String getAdSpaceNname(String adspace);
}
