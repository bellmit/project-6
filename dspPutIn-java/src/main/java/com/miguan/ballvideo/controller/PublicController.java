package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.common.constants.ShapeNameParam;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.VideoCatService;
import com.miguan.ballvideo.service.dsp.AdvertAreaService;
import com.miguan.ballvideo.service.dsp.AdvertPhoneService;
import com.miguan.ballvideo.vo.response.DistrictRes;
import com.miguan.ballvideo.vo.response.PhoneBrandRes;
import com.miguan.ballvideo.vo.response.VideoCatRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@Api(value="公共接口 Controller",tags={"公共接口"})
@RestController
@RequestMapping("/api/public")
public class PublicController {


    @Resource
    AdvertAreaService advertAreaService;
    @Resource
    AdvertPhoneService advertPhoneService;
    @Resource
    VideoCatService videoCatService;

    @ApiOperation("获得省份列表")
    @GetMapping("/getProvinceList")
    public ResultMap<List<DistrictRes>> getProvinceList() {
        try {
            return ResultMap.success(advertAreaService.getProvinceList());
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获得城市列表")
    @GetMapping("/getCityListByProvince")
    public ResultMap<List<DistrictRes>> getCityListByProvince(@ApiParam(value = "父节点id") Long pid) {
        try {
            return ResultMap.success(advertAreaService.getCityListByProvince(pid));
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获得手机列表")
    @GetMapping("/getPhoneList")
    public ResultMap<List<PhoneBrandRes>> getPhoneList() {
        try {
            return ResultMap.success(advertPhoneService.findAll());
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
    @ApiOperation("获得创意类型列表")
    @GetMapping("/getMaterialShapeList")
    public ResultMap<List<ShapeNameParam>> getMaterialShapeList() {
        try {
            return ResultMap.success(MaterialShapeConstants.materialShapeNameList);
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获得兴趣列表")
    @GetMapping("/getInterestList")
    public ResultMap<List<VideoCatRes>> getInterestList() {
        try {
            return ResultMap.success(videoCatService.findAll());
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
