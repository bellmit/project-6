package com.miguan.xuanyuan.controller.front;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.CustomField;
import com.miguan.xuanyuan.dto.XyAdPositionDto;
import com.miguan.xuanyuan.entity.XyAdPosition;
import com.miguan.xuanyuan.service.XyAdPositionService;
import com.miguan.xuanyuan.vo.OptionsVo;
import com.miguan.xuanyuan.vo.XyAdPositionSimpleVo;
import com.miguan.xuanyuan.vo.XyAdPositionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 广告位
 * @Date 2021/1/21
 **/
@Api(value = "广告位(前台)控制层", tags = {"广告位(前台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/xyAdPosition")
public class XyAdPositionFrontController extends AuthBaseController {

    @Resource
    private XyAdPositionService service;


    @ApiOperation("分页查询广告位列表")
    @GetMapping("/pageList")
    public ResultMap<PageInfo<XyAdPositionVo>> pageList(@ApiParam("类型:1.广告位名称,2.广告位key,3.应用名称,4.应用key") Integer type,
                                                       @ApiParam("内容：类型(type)对应的值") String keyword,
                                                       @ApiParam("操作系统:1.Android，2.ios") Integer clientType,
                                                       @ApiParam("状态，1已启用，0已禁用") Integer status,
                                                       @ApiParam("广告样式") String adType,
                                                       @ApiParam(value="页码", required=true) Integer pageNum,
                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        if(getCurrentUser() == null){
            return ResultMap.error("必须先登录！");
        }
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        return ResultMap.success(service.pageList(XyConstant.FRONT_PLAT, getCurrentUser().getUserId(),null,type,keyword, clientType ,status ,adType , pageNum, pageSize));
    }

    @ApiOperation("查询广告位列表(已启用)")
    @GetMapping("/list")
    public ResultMap<List<XyAdPositionSimpleVo>> list(@ApiParam("应用id") Long appId) {
        return ResultMap.success(service.findList(XyConstant.FRONT_PLAT, getCurrentUser().getUserId(),appId));
    }

    @ApiOperation("新增、编辑广告位")
    @PostMapping("/save")
    @LogInfo(pathName="前台-广告位管理-新增、编辑广告位",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap save(@ApiParam("广告位实体类") @RequestBody @Valid XyAdPositionDto xyAdPositionDto) {
        try {
            if(getCurrentUser() == null){
                return ResultMap.error("必须先登录！");
            } else {
                xyAdPositionDto.setUserId(getCurrentUser().getUserId());
            }
            XyAdPosition xyAdPosition = init(xyAdPositionDto);
            service.save(xyAdPosition);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    private XyAdPosition init(XyAdPositionDto xyAdPositionDto) {
        //初始化默认值
        if(xyAdPositionDto.getNote() == null){
            xyAdPositionDto.setNote("");
        }
        XyAdPosition xyAdPosition = new XyAdPosition();
        BeanUtils.copyProperties(xyAdPositionDto,xyAdPosition);
        if(CollectionUtils.isNotEmpty(xyAdPositionDto.getCustomFields())){
            xyAdPosition.setCustomField(JSON.toJSONString(xyAdPositionDto.getCustomFields()));
        } else {
            xyAdPosition.setCustomField("");
        }
        return xyAdPosition;
    }

    @ApiOperation("查询广告位信息")
    @GetMapping("/info")
    public ResultMap<XyAdPositionDto> info(@ApiParam("广告位实体类") Long id) {
        try {
            if(id == null){
                return ResultMap.error("必须传入广告位主键");
            }
            XyAdPosition xyAdPosition = service.findById(id);
            XyAdPositionDto xyAdPositionDto = new XyAdPositionDto();
            if(xyAdPosition != null){
                BeanUtils.copyProperties(xyAdPosition,xyAdPositionDto);
                if(xyAdPosition != null && StringUtils.isNotEmpty(xyAdPosition.getCustomField())){
                    xyAdPositionDto.setCustomFields(JSON.parseArray(xyAdPosition.getCustomField(), CustomField.class));
                }
            }
            return ResultMap.success(xyAdPositionDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("删除广告位")
    @GetMapping("/delete")
    @LogInfo(pathName="前台-广告位管理-删除广告位",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_DELETE)
    public ResultMap delete(@ApiParam("广告位主键") Long id) {
        try {
            if(id == null){
                return ResultMap.error("必须传入广告位主键");
            }

            service.deleteById(id);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取应用/广告位联动选择项")
    @GetMapping("/linkageSelection")
    public ResultMap linkageSelection() {
        try {
            List<OptionsVo> optionsVo = service.linkageSelection(XyConstant.FRONT_PLAT,getCurrentUser().getUserId());
            return ResultMap.success(optionsVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
