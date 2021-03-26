package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.dto.PermissionListDto;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.entity.Identity;
import com.miguan.xuanyuan.vo.AdminPermissionVo;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
public interface AdminPermissionService extends IService<AdminPermission> {
    void saveData(AdminPermission adminPermission);

    AdminPermission getPermissionExcludeId(String actionCode, Long id);

    List<AdminPermissionVo> getPermissionListByParentId(Long parentId);

    Integer existChildren(Long parentId);

    Integer projectHasChildren(Long projectId);

    /**
     * 递归查询出全部菜单树
     * @return
     */
    List<PermissionListDto> listTreePermission();
}
