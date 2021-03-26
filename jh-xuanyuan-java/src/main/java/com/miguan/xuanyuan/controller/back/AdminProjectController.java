package com.miguan.xuanyuan.controller.back;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.service.AdminPermissionService;
import com.miguan.xuanyuan.service.AdminProjectService;
import com.miguan.xuanyuan.vo.AdminPermissionVo;
import com.miguan.xuanyuan.vo.AdminProjectVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 项目表 前端控制器
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@RestController
@RequestMapping("/api/back/project")
public class AdminProjectController {

    @Resource
    AdminProjectService adminProjectService;

    @Resource
    AdminPermissionService adminPermissionService;

    @ApiOperation("获取项目列表列表")
    @GetMapping(value = {"/getProjectList"})
    public ResultMap getProjectList() {
        List<AdminProjectVo> list = adminProjectService.getProjectList();

        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(vo -> {
                Long projectId = vo.getId();
                vo.setHasChildren(0);
                Integer childrenCnt = adminPermissionService.projectHasChildren(projectId);
                if (childrenCnt !=null && childrenCnt > 0) {
                    vo.setHasChildren(1);
                }
            });
        }
        return ResultMap.success(list);
    }
}
