package com.miguan.laidian.service;

import com.github.pagehelper.Page;
import com.miguan.laidian.entity.ImagesCat;
import com.miguan.laidian.vo.ImagesVo;

import java.util.List;
import java.util.Map;

/**
 * 图片列表列表Service
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

public interface ImagesService {

    /**
     * 查询图片分类
     *
     * @return
     */
    List<ImagesCat> findAll();

    /**
     * 通过条件查询图片列表
     **/
    Page<ImagesVo> findImagesList(Map<String, Object> params, int currentPage, int pageSize);

    /**
     * 修改图片收藏、分享数量
     **/
    int updateCount(Map<String, Object> params);

}