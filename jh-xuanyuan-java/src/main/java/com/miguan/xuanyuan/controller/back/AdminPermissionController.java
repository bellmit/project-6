package com.miguan.xuanyuan.controller.back;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.PermissionListDto;
import com.miguan.xuanyuan.dto.request.AdminPermissionRequest;
import com.miguan.xuanyuan.dto.request.AdminPermissionStatusRequest;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.entity.AdminProject;
import com.miguan.xuanyuan.service.AdminPermissionService;
import com.miguan.xuanyuan.service.AdminProjectService;
import com.miguan.xuanyuan.vo.AdminPermissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Api(value = "菜单权限(后台)", tags = {"菜单权限(后台)接口"})
@RestController
@RequestMapping("/api/back/auth")
public class AdminPermissionController extends AuthBaseController {

    @Resource
    AdminPermissionService adminPermissionService;

    @Resource
    AdminProjectService adminProjectService;


    @ApiOperation("获取权限详情")
    @GetMapping(value = {"/detail"})
    public ResultMap getData(@RequestParam("id") Long id) {

        if (id == null || id <= 0l) {
            return ResultMap.error("id错误");
        }

        AdminPermission adminPermission = adminPermissionService.getById(id);
        if (adminPermission == null) {
            return ResultMap.error("权限不存在或已被删除");
        }
        AdminPermissionVo vo = new AdminPermissionVo();
        BeanUtils.copyProperties(adminPermission, vo);

        vo.setHasChildren(0);
        Integer childrenCnt = adminPermissionService.existChildren(id);
        if (childrenCnt !=null && childrenCnt > 0) {
            vo.setHasChildren(1);
        }

        return ResultMap.success(vo);
    }

    @ApiOperation("添加权限操作")
    @PostMapping(value = {"/addPermission"})
    @LogInfo(pathName="后台-平台配置-添加权限操作",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_ADD)
    public ResultMap add(@RequestBody @Valid AdminPermissionRequest adminPermissionRequest) {

        try {
            adminPermissionRequest.checkAdd();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        }

        AdminProject adminProject = adminProjectService.getById(adminPermissionRequest.getProjectId());
        if (adminProject == null) {
            return ResultMap.error("projectId错误");
        }

        if (adminPermissionRequest.getParentId() > 0) {
            AdminPermission parentData = adminPermissionService.getById(adminPermissionRequest.getParentId());
            if (parentData == null || parentData.getStatus() == XyConstant.STATUS_CLOSE) {
                return ResultMap.error("父节点数据不存在");
            }
        }

        AdminPermission adminPermissionExist = adminPermissionService.getPermissionExcludeId(adminPermissionRequest.getActionCode(), adminPermissionRequest.getId());
        if (adminPermissionExist != null) {
            return ResultMap.error("actionCode不能重复");
        }

        AdminPermission adminPermission = adminPermissionRequest.convertAdminPermission();
        try {
            adminPermissionService.save(adminPermission);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }

        AdminPermissionVo vo = new AdminPermissionVo();
        BeanUtils.copyProperties(adminPermission, vo);
        return ResultMap.success(vo);
    }

    @ApiOperation("修改权限操作")
    @PostMapping(value = {"/editPermission"})
    @LogInfo(pathName="后台-平台配置-修改权限操作",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap edit(@RequestBody @Valid AdminPermissionRequest adminPermissionRequest) {

        try {
            adminPermissionRequest.checkEdit();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        }

        AdminProject adminProject = adminProjectService.getById(adminPermissionRequest.getProjectId());
        if (adminProject == null) {
            return ResultMap.error("projectId错误");
        }

        AdminPermission adminPermissionData = adminPermissionService.getById(adminPermissionRequest.getId());
        if (adminPermissionData == null) {
            return ResultMap.error("数据不存在");
        }

        AdminPermission adminPermissionExist = adminPermissionService.getPermissionExcludeId(adminPermissionRequest.getActionCode(), adminPermissionRequest.getId());
        if (adminPermissionExist != null) {
            ResultMap.error("actionCode不能重复");
        }

        AdminPermission adminPermission = adminPermissionRequest.convertAdminPermission();
        try {
            boolean result = adminPermissionService.updateById(adminPermission);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
        AdminPermissionVo vo = new AdminPermissionVo();
        BeanUtils.copyProperties(adminPermission, vo);
        return ResultMap.success(vo);
    }

    @ApiOperation("获取权限列表")
    @GetMapping(value = {"/getPermissionList"})
    public ResultMap getPermissionList(@RequestParam("projectId") Long projectId, @RequestParam("parentId") Long parentId) {

        if (parentId == null) {
            return ResultMap.error("parentId数据不能为空");
        }
        List<AdminPermissionVo> list = adminPermissionService.getPermissionListByParentId(parentId);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(vo -> {
                Long id = vo.getId();
                vo.setHasChildren(0);
                Integer childrenCnt = adminPermissionService.existChildren(id);
                if (childrenCnt !=null && childrenCnt > 0) {
                    vo.setHasChildren(1);
                }
            });
        }
        return ResultMap.success(list);
    }

    @ApiOperation("获取权限菜单树结构")
    @GetMapping(value = {"/listTreePermission"})
    public ResultMap<List<PermissionListDto>> listTreePermission() {
        List<PermissionListDto> list = adminPermissionService.listTreePermission();
        return ResultMap.success(list);
    }

    @ApiOperation("更新状态")
    @PostMapping(value = {"/changeStatus"})
    @LogInfo(pathName="后台-平台配置-更新状态",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap changeStatus(@RequestBody @Valid AdminPermissionStatusRequest adminPermissionStatusRequest) {
        AdminPermission adminPermissionData = adminPermissionService.getById(adminPermissionStatusRequest.getId());
        if (adminPermissionData == null) {
            return ResultMap.error("数据不存在");
        }
        adminPermissionData.setStatus(adminPermissionStatusRequest.getStatus());

        try {
            boolean result = adminPermissionService.updateById(adminPermissionData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
        AdminPermissionVo vo = new AdminPermissionVo();
        BeanUtils.copyProperties(adminPermissionData, vo);
        return ResultMap.success(vo);
    }



}
