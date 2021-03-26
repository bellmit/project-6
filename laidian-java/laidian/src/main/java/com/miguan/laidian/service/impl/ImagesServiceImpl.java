package com.miguan.laidian.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.entity.ImagesCat;
import com.miguan.laidian.mapper.ImagesMapper;
import com.miguan.laidian.repositories.ImagesCatDao;
import com.miguan.laidian.service.ImagesService;
import com.miguan.laidian.vo.ImagesVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 视频源列表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

@Service("imagesService")
public class ImagesServiceImpl implements ImagesService {

    @Resource
    private ImagesMapper imagesMapper;

    @Resource
    private ImagesCatDao imagesCatDao;

    @Override
    public List<ImagesCat> findAll() {
        return imagesCatDao.findAllByOrderBySortAsc();
    }

    @Override
    public Page<ImagesVo> findImagesList(Map<String, Object> params, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<ImagesVo> imagesList = imagesMapper.findImagesList(params);
        int detailPageCount = Global.getInt("detail_page_count",MapUtils.getString(params, "appType"));//iOS来电详情广告位次数
        for (ImagesVo imagesVo : imagesList) {
            imagesVo.setDetailPageCount(detailPageCount);
        }
        return (Page<ImagesVo>)imagesList;
    }

    @Override
    public int updateCount(Map<String, Object> params) {
        return imagesMapper.updateCount(params);
    }
}