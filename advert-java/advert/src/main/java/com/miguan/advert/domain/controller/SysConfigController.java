package com.miguan.advert.domain.controller;

import com.github.pagehelper.PageInfo;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.domain.dto.SysConfigDto;
import com.miguan.advert.domain.service.AccountService;
import com.miguan.advert.domain.service.SysConfigService;
import com.miguan.advert.domain.vo.request.AccountAddVo;
import com.miguan.advert.domain.vo.request.AccountUpdateVo;
import com.miguan.advert.domain.vo.result.AccountDetailInfoVo;
import com.miguan.advert.domain.vo.result.AccountListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Api(value = "开关配置",tags = {"开关配置"})
@RestController
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @ApiOperation(value = "开关配置查询列表")
    @PostMapping("/api/sys/config/listSysConfig")
    public PageInfo<SysConfigDto> listSysConfig(@ApiParam(value = "类型,10-开关类型，20--业务参数，30--第三方配置") Integer type,
                                                @ApiParam(value = "配置名称") String name,
                                                @ApiParam(value = "状态：-1不启用，1启用") Integer status,
                                                @ApiParam(value = "页码", required = true) int pageNum,
                                                @ApiParam(value = "每页记录数", required = true) int pageSize) {
        return sysConfigService.listSysConfig(type, name, status, pageNum, pageSize);
    }

    @ApiOperation(value = "根据id查询单个开关配置信息")
    @PostMapping("/api/sys/config/getSysConfig")
    public SysConfigDto getSysConfig(@ApiParam(value = "开关配置id") Long id) {
        return sysConfigService.getSysConfig(id);
    }

    @ApiOperation(value = "新增或修改配置(id为空，则是新增；id不为空，则是修改)")
    @PostMapping("/api/sys/config/saveSysConfig")
    public void saveSysConfig(SysConfigDto dto) {
        sysConfigService.saveSysConfig(dto);
    }

    @ApiOperation(value = "删除配置")
    @PostMapping("/api/sys/config/deleteSysConfig")
    public void deleteSysConfig(@ApiParam(value = "配置code") String code) {
        sysConfigService.deleteSysConfig(code);
    }
}
