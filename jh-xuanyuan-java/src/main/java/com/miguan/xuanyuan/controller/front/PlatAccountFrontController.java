package com.miguan.xuanyuan.controller.front;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.PlatAccountDto;
import com.miguan.xuanyuan.dto.PlatAccountListDto;
import com.miguan.xuanyuan.service.XyPlatAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tool.util.StringUtil;

import javax.annotation.Resource;

@Api(value = "平台账号(前台)", tags = {"平台账号(前台)接口"})
@RestController
@Slf4j
@RequestMapping("/api/front/plat/account")
public class PlatAccountFrontController extends AuthBaseController {

    @Resource
    private XyPlatAccountService xyPlatAccountService;

    @ApiOperation("广告平台账号列表")
    @GetMapping("/findPlatAccountList")
    public ResultMap<PageInfo<PlatAccountListDto>> findPlatAccountList(@ApiParam(value="页码", required=true) Integer pageNum,
                                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        Long userId = getCurrentUser().getUserId();
        PageInfo<PlatAccountListDto> pageList = xyPlatAccountService.findAccountList(null, userId, pageNum, pageSize);
        return ResultMap.success(pageList);
    }

    @ApiOperation("保存平台账号（根据id进行新增或修改操作）")
    @PostMapping("/savePlatAccount")
    @LogInfo(pathName="前台-广告平台管理-保存平台账号",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap savePlatAccount(@RequestBody @Validated PlatAccountDto platAccountDto) {
        this.verification(platAccountDto);  //表单校验
        Long userId = getCurrentUser().getUserId();
        platAccountDto.setUserId(userId);
        xyPlatAccountService.savePlatAccount(platAccountDto);
        return ResultMap.success();
    }

    /**
     * 校验表单
     * @param dto
     */
    private void verification(PlatAccountDto dto) {
        if(dto.getOpenReportapi() == 1 && StringUtil.isBlank(dto.getAppId())) {
            throw new CommonException("appId不能为空");
        }
        if(dto.getOpenReportapi() == 1 && StringUtil.isBlank(dto.getAppSecret())) {
            throw new CommonException("appSecret不能为空");
        }
    }

    @ApiOperation("根据id查询平台账号信息")
    @GetMapping("/getPlatAccount")
    public ResultMap<PlatAccountDto> getPlatAccount(Long id) {
        PlatAccountDto platAccountDto = xyPlatAccountService.getPlatAccount(id);
        return ResultMap.success(platAccountDto);
    }
}
