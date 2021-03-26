package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.LdInterestTag;
import com.miguan.laidian.service.LdInterestTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 10:35
 * @Description:标签管理
 */
@Api(value = "标签管理Api", tags = {"标签管理"})
@RestController
@RequestMapping("/api/tag")
public class TagController {
    @Resource
    private LdInterestTagService ldInterestTagService;

    /**
     * 获取用户标签
     * @param userId
     * @return
     */
    @ApiOperation("获取用户标签")
    @GetMapping("/getUserTags")
    public ResultMap getUserTags(String userId) {
        List<LdInterestTag> list = ldInterestTagService.getUserTags(userId);
        return ResultMap.success(list, "成功");
    }

    @ApiOperation("保存用户标签")
    @PostMapping("/saveTag")
    public ResultMap saveTag(String tagIds, @ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        try {
            if (tagIds == "" || null == tagIds) {
                return ResultMap.error("请选择标签");
            }

            Integer result = ldInterestTagService.saveTag(commomParams.getUserId(), tagIds, commomParams.getDeviceId());
            return ResultMap.success(result, "保存成功");
        } catch (Exception exception) {
            return ResultMap.error(exception.getMessage());
        }
    }

    /**
     * 获取全部标签
     * @return
     */
    @ApiOperation("获取全部标签")
    @GetMapping("/getAllList")
    public ResultMap getAllList() {
        List<LdInterestTag> list = ldInterestTagService.getAllList();
        return ResultMap.success(list, "成功");
    }
}
