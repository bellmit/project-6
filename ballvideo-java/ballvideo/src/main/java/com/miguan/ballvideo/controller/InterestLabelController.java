package com.miguan.ballvideo.controller;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.ballvideo.common.enums.InterestLabelEnum;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.IssueInterestLabelDto;
import com.miguan.ballvideo.service.InterestLabelService;
import com.miguan.ballvideo.vo.SaveLabelInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value="兴趣标签接口",tags={"兴趣标签接口"})
@RestController
@RequestMapping("/api/interestLabel")
public class InterestLabelController {

    @Resource
    private InterestLabelService interestLabelService;

    @ApiOperation("保存用户兴趣标签信息")
    @PostMapping("/saveLabelInfo")
    public ResultMap saveLabelInfo(@Validated SaveLabelInfo labelInfo,
                                   @RequestHeader(value = "Public-Info") String publicInfo) {
        int result = interestLabelService.saveLabelInfo(labelInfo,publicInfo);
        if (result != 1) {
            return ResultMap.error("保存用户兴趣标签信息错误！");
        }
        return ResultMap.success();
    }

    /**
     * 根据实验组下发兴趣标签
     * @param experimentType 实验组编号
     * @return
     */
    @ApiOperation(value = "下发兴趣标签")
    @GetMapping ("/issueInterestLabel")
    public ResultMap<List<IssueInterestLabelDto>> issueInterestLabel(@ApiParam(value = "实验组变量 1 无兴趣标签 2 实验组一 3 实验组二",required = true) Integer experimentType,
                                                                     @RequestHeader(value = "deviceId") String deviceId,
                                                                     @RequestHeader(value = "channelId") String channelId) {
        if(experimentType == null ){
            throw new CommonException("参数experimentType异常");
        }
        boolean result = interestLabelService.getLabelInfo(deviceId, channelId);
        if(experimentType == 1 || !result){
            return ResultMap.success();
        } else{
            return ResultMap.success(InterestLabelEnum.exportList(experimentType));
        }
    }
}
