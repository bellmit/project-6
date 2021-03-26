package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.RdPage;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.ImagesCat;
import com.miguan.laidian.service.ImagesService;
import com.miguan.laidian.vo.ImagesVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片Controller
 *
 * @Author xy.chen
 * @Date 2019/7/9
 **/
@Api(value = "图片controller", tags = {"图片接口"})
@RestController
@RequestMapping("/api/image")
public class ImagesController {

    @Resource
    private ImagesService imagesService;

    /**
     * 图片分类
     *
     * @return
     */
    @ApiOperation(value = "图片分类")
    @PostMapping("/imagesCatList")
    public ResultMap imagesCatList() {
        List<ImagesCat> imagesCatList = imagesService.findAll();
        return ResultMap.success(imagesCatList);
    }

    /**
     * 图片列表
     * @param commomParams 公共参数
     * @param category 图片分类ID
     * @param imageType 类型：10--推荐 30--分类
     * @return
     */
    @ApiOperation(value = "图片列表")
    @PostMapping("/imagesList")
    public ResultMap imagesList(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("图片分类ID") String category,
                                @ApiParam("类型：10--推荐 30--分类") String imageType) {
        Map<String, Object> params = new HashMap<>();
        if ("30".equals(imageType)){
            params.put("category", category);
        }
        params.put("state", Constant.open);//状态 1开启 2关闭
        params.put("imageType", imageType);
        params.put("appType", commomParams.getAppType());
        Page<ImagesVo> page = imagesService.findImagesList(params, commomParams.getCurrentPage(), commomParams.getPageSize());
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(page));
        result.put("data", page);
        return ResultMap.success(result);
    }

    /**
     * 更新用户收藏数量、点击数
     * @param id
     * @param opType
     * @return
     */
    @ApiOperation(value = "更新用户收藏数量、点击数")
    @PostMapping("/updateCount")
    public ResultMap updateCount(@ApiParam("图片ID") @RequestParam("id") String id,
                                 @ApiParam("操作类型：10--收藏 30--取消收藏 40--点击") String opType) {
        Map<String, Object> params = new HashMap<>();
        params.put("opType", opType);
        params.put("id", id);
        int num = imagesService.updateCount(params);
        if (num>0)return ResultMap.success();
        return ResultMap.error();
    }
}
